package dev.olog.music_service

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.service.BookmarkUseCase
import dev.olog.domain.interactor.service.CurrentSongIdUseCase
import dev.olog.domain.interactor.service.GetPlayingQueueUseCase
import dev.olog.domain.interactor.service.UpdatePlayingQueueUseCase
import dev.olog.music_service.di.PerService
import dev.olog.music_service.interfaces.Queue
import dev.olog.music_service.model.*
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.shuffle
import dev.olog.shared.shuffleAndSwap
import dev.olog.shared.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toFlowable
import java.util.*
import javax.inject.Inject

@PerService
class QueueImpl @Inject constructor(
        private val getPlayingQueueUseCase: GetPlayingQueueUseCase,
        private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentSongIdUseCase,
        private val repeatMode: RepeatMode,
        private val shuffleMode: ShuffleMode,
        private val bookmarkUseCase: BookmarkUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : Queue {

    companion object {
        private const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000
    }

    private val playingQueue = Vector<MediaEntity>()

    private var savePlayingQueueDisposable: Disposable? = null

    private var currentSongPosition = -1

    override fun prepare(): Single<Pair<PlayerMediaEntity, Long>> {
        return getPlayingQueueUseCase.execute()
                .flatMap{ it.toFlowable().map { it.toMediaEntity() }.toList() }
                .doOnSuccess(this::updatePlayingQueue)
                .map(this::getCurrentSong)
                .doOnSuccess { (position, metadata) -> updateCurrentPosition(position, metadata.id) }
                .map { (position, metadata) -> metadata.toPlayerMediaEntity(computePositionInQueue(position))
                        .to(bookmarkUseCase.get())
                }
    }

    override fun handleSkipToQueueItem(id: Long): PlayerMediaEntity {
        var position = playingQueue.indexOfFirst { it.id == id }
        position = ensurePosition(position)
        val mediaEntity = playingQueue[position]
        updateCurrentPosition(position, mediaEntity.id)

        return mediaEntity.toPlayerMediaEntity(getCurrentPositionInQueue())
    }

    override fun handleSkipToNext(): PlayerMediaEntity {
        return if (repeatMode.isRepeatOne()) {
            getPlayingSong()
        } else getSongAtPosition(currentSongPosition + 1)
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity {
        return if (playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD || repeatMode.isRepeatOne()) {
            getPlayingSong()
        } else getSongAtPosition(currentSongPosition - 1)
    }

    override fun handlePlayFromMediaId(mediaId: String): Single<PlayerMediaEntity> {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flatMap{ it.toFlowable().map { it.toMediaEntity() }.toList() }
                .map { it.shuffleAndSwap(shuffleMode.isEnabled(), { it.id == songId }) }
                .doOnSuccess { updatePlayingQueue(it) }
                .map { if (shuffleMode.isEnabled()){
                    getSongByPosition(it, 0)
                } else {  getSongById(it, songId)
                } }
                .doOnSuccess { (position, metadata) -> updateCurrentPosition(position, metadata.id) }
                .map { (position, metadata) -> metadata.toPlayerMediaEntity(computePositionInQueue(position)) }
    }

    override fun handlePlayShuffle(bundle: Bundle): Single<PlayerMediaEntity> {
        val mediaId = bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flatMap{ it.toFlowable().map { it.toMediaEntity() }.toList() }
                .map { it.shuffle() }
                .doOnSuccess { updatePlayingQueue(it) }
                .map { getSongByPosition(it, 0) }
                .doOnSuccess { (position, metadata) -> updateCurrentPosition(position, metadata.id) }
                .map { (position, metadata) -> metadata.toPlayerMediaEntity(computePositionInQueue(position)) }
                .doOnSuccess { shuffleMode.setEnabled(true) }
    }

    override fun handlePlayFirst(bundle: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePlayFromSearch(extras: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sort() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shuffle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getPlayingSong(): PlayerMediaEntity {
        return playingQueue[currentSongPosition].toPlayerMediaEntity(getCurrentPositionInQueue())
    }

    private fun getSongAtPosition(position: Int): PlayerMediaEntity {
        updateCurrentPosition(ensurePosition(position))

        return playingQueue[currentSongPosition].toPlayerMediaEntity(computePositionInQueue(position))
    }


    private fun updatePlayingQueue(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private fun persist(songList: List<MediaEntity>) {
        savePlayingQueueDisposable.unsubscribe()
        savePlayingQueueDisposable = songList.toFlowable()
                .map { it.id }
                .toList()
                .flatMapCompletable { updatePlayingQueueUseCase.execute(it) }
                .subscribe({}, Throwable::printStackTrace)
    }

    private fun getCurrentSong(list: List<MediaEntity>): Pair<Int, MediaEntity> {
        val currentSongId = currentSongIdUseCase.get()
        return getSongById(list, currentSongId)
    }

    private fun getSongById(list: List<MediaEntity>, songId: Long) : Pair<Int, MediaEntity> {
        val position = list.indexOfFirst { it.id == songId }
        return getSongByPosition(list, position)
    }

    private fun getSongByPosition(list: List<MediaEntity>, position: Int) : Pair<Int, MediaEntity> {
        val songPosition = ensurePosition(position)
        return songPosition.to(list[songPosition])
    }

    private fun ensurePosition(position: Int): Int {
        if (position < 0) {
            return if (repeatMode.isRepeatAll) playingQueue.lastIndex else 0
        }
        return if (position > playingQueue.lastIndex) {
            if (repeatMode.isRepeatAll) 0 else playingQueue.lastIndex
        } else position
    }

    private fun updateCurrentPosition(position: Int){
        updateCurrentPosition(position, playingQueue[position].id)
    }

    private fun updateCurrentPosition(position: Int, songId: Long) {
        currentSongPosition = position
        currentSongIdUseCase.set(songId)
    }

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return computePositionInQueue(currentSongPosition)
    }

    private fun computePositionInQueue(position: Int): PositionInQueue {
        return when {
            repeatMode.isRepeatAll || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            currentSongPosition == 0 && currentSongPosition == playingQueue.lastIndex -> PositionInQueue.BOTH
            currentSongPosition == 0 -> PositionInQueue.FIRST
            currentSongPosition == playingQueue.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }

}