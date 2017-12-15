package dev.olog.music_service

import android.os.Bundle
import android.support.v4.math.MathUtils
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.most_played.GetMostPlayedSongsUseCase
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.domain.interactor.music_service.BookmarkUseCase
import dev.olog.domain.interactor.music_service.CurrentSongIdUseCase
import dev.olog.domain.interactor.music_service.GetPlayingQueueUseCase
import dev.olog.music_service.interfaces.Queue
import dev.olog.music_service.model.*
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap
import dev.olog.shared.shuffle
import dev.olog.shared.swap
import io.reactivex.Single
import io.reactivex.functions.Function
import javax.inject.Inject

class QueueManager @Inject constructor(
        private val queueImpl: QueueImpl,
        private val getPlayingQueueUseCase: GetPlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentSongIdUseCase,
        private val bookmarkUseCase: BookmarkUseCase,
        private val repeatMode: RepeatMode,
        private val shuffleMode: ShuffleMode,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getMostPlayedSongsUseCase: GetMostPlayedSongsUseCase,
        private val getRecentlyAddedUseCase: GetRecentlyAddedUseCase

) : Queue {

    override fun prepare(): Single<Pair<PlayerMediaEntity, Long>> {
        return getPlayingQueueUseCase.execute()
                .groupMap { it.toMediaEntity("") }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .doOnSuccess {  }
                .map { currentLastPlayedSong.apply(it) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
                .map { it.to(MathUtils.clamp(bookmarkUseCase.get().toInt(),
                        0, it.mediaEntity.duration.toInt()).toLong()) }
    }

    override fun handleSkipToQueueItem(id: Long): PlayerMediaEntity {
        return queueImpl.getSongById(id).toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handleSkipToNext(): PlayerMediaEntity {
        return queueImpl.getNextSong().toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity {
        return queueImpl.getPreviousSong(playerBookmark).toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handlePlayFromMediaId(mediaId: String): Single<PlayerMediaEntity> {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .groupMap { it.toMediaEntity(mediaId) }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) ->  list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
    }

    override fun handlePlayRecentlyPlayed(mediaId: String): Single<PlayerMediaEntity> {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()

        return getRecentlyAddedUseCase.execute(mediaId)
                .firstOrError()
                .groupMap { it.toMediaEntity(mediaId) }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) ->  list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
    }

    override fun handlePlayMostPlayed(mediaId: String): Single<PlayerMediaEntity> {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()

        return getMostPlayedSongsUseCase.execute(mediaId)
                .firstOrError()
                .groupMap { it.toMediaEntity(mediaId) }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) ->  list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
    }

    override fun handlePlayShuffle(bundle: Bundle): Single<PlayerMediaEntity> {
        val mediaId = bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .groupMap { it.toMediaEntity(mediaId) }
                .map { it.shuffle() }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { Pair(it, 0) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list,position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
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
        return computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
    }

    override fun addItemToQueue(item: MediaDescriptionCompat) {
        queueImpl.addItemToQueue(item)
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