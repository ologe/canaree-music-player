package dev.olog.music_service

import android.os.Bundle
import android.support.v4.math.MathUtils
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.service.BookmarkUseCase
import dev.olog.domain.interactor.service.CurrentSongIdUseCase
import dev.olog.domain.interactor.service.GetPlayingQueueUseCase
import dev.olog.music_service.interfaces.Queue
import dev.olog.music_service.model.*
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.shuffle
import dev.olog.shared.swap
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class QueueManager @Inject constructor(
        private val queueImpl: QueueImpl,
        private val getPlayingQueueUseCase: GetPlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentSongIdUseCase,
        private val bookmarkUseCase: BookmarkUseCase,
        private val repeatMode: RepeatMode,
        private val shuffleMode: ShuffleMode,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : Queue {

    override fun prepare(): Single<Pair<PlayerMediaEntity, Long>> {
        return getPlayingQueueUseCase.execute()
                .flatMap { mapToMediaEntityAndPersist.apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueue)
                .map { currentLastPlayedSong.apply(it) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) -> list[position] }
                .map { it.toPlayerMediaEntity(computePositionInQueue(0, listOf())) }
                .map { it.to(bookmarkUseCase.get()) }
    }

    override fun handleSkipToQueueItem(id: Long): PlayerMediaEntity {
        return queueImpl.getSongById(id).toPlayerMediaEntity(computePositionInQueue(0, listOf()))
    }

    override fun handleSkipToNext(): PlayerMediaEntity {
        return queueImpl.getNextSong().toPlayerMediaEntity(computePositionInQueue(0, listOf()))
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity {
        return queueImpl.getPreviousSong(playerBookmark).toPlayerMediaEntity(computePositionInQueue(0, listOf()))
    }

    override fun handlePlayFromMediaId(mediaId: String): Single<PlayerMediaEntity> {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flatMap { mapToMediaEntityAndPersist.apply(it) }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) -> list[position] }
                .map { it.toPlayerMediaEntity(computePositionInQueue(0, listOf())) }
    }

    override fun handlePlayShuffle(bundle: Bundle): Single<PlayerMediaEntity> {
        val mediaId = bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flatMap { mapToMediaEntityAndPersist.apply(it) }
                .map { it.shuffled() }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { Pair(it, 0) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list,position) }
                .map { (list, position) -> list[position] }
                .map { it.toPlayerMediaEntity(computePositionInQueue(0, listOf())) }
                .doOnSuccess { shuffleMode.setEnabled(true) }
    }

    override fun handlePlayFromSearch(extras: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sort() {
        queueImpl.sort()
    }

    override fun shuffle() {
        queueImpl.shuffle()
    }

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return computePositionInQueue(queueImpl.currentSongPosition, listOf())
    }

    override fun addItemToQueue(item: MediaDescriptionCompat) {
        queueImpl.addItemToQueue(item)
    }

    private val mapToMediaEntityAndPersist = Function<List<Song>, SingleSource<List<MediaEntity>>> {
        it.toFlowable().map { it.toMediaEntity() }.toList()
    }

    private val currentLastPlayedSong = Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
        val songId = currentSongIdUseCase.get()
        val currentPosition = MathUtils.clamp(list.indexOfFirst { it.id == songId }, 0, list.lastIndex)
        Pair(list, currentPosition)
    }

    private fun getCurrentSongOnPlayFromId(songId: Long) = Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
        if (shuffleMode.isEnabled()){
            Pair(list, 0)
        } else {
            val position = MathUtils.clamp(list.indexOfFirst { it.id == songId }, 0, list.lastIndex)
            Pair(list, position)
        }
    }

    private fun shuffleIfNeeded(songId: Long) = Function<List<MediaEntity>, List<MediaEntity>> { list ->
        if (shuffleMode.isEnabled()){
            val item = list.first { it.id == songId }
            list.shuffle()
            val songPosition = list.indexOf(item)
            if (songPosition != 0){
                list.swap(0, songPosition)
            }
        }
        list
    }

    private fun computePositionInQueue(position: Int, list: List<MediaEntity>): PositionInQueue {
        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            position == 0 && position == list.lastIndex -> PositionInQueue.BOTH
            position == 0 -> PositionInQueue.FIRST
            position == list.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }
}