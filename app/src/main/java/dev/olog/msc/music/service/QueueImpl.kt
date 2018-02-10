package dev.olog.msc.music.service

import android.support.annotation.CheckResult
import android.support.annotation.MainThread
import android.support.v4.math.MathUtils
import dev.olog.msc.domain.interactor.music.service.CurrentIdInPlaylistUseCase
import dev.olog.msc.domain.interactor.music.service.UpdatePlayingQueueUseCase
import dev.olog.msc.domain.interactor.music.service.UpdatePlayingQueueUseCaseRequest
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.music.service.model.PositionInQueue
import dev.olog.msc.utils.k.extension.shuffle
import dev.olog.msc.utils.k.extension.swap
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.shared_android.assertMainThread
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject

private const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

class QueueImpl @Inject constructor(
        private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
        private val repeatMode: RepeatMode,
        private val currentSongIdUseCase: CurrentIdInPlaylistUseCase,
        private val queueMediaSession: QueueMediaSession
) {

    private var savePlayingQueueDisposable: Disposable? = null

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition = -1

    fun updatePlayingQueueAndPersist(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private fun persist(songList: List<MediaEntity>) {
        savePlayingQueueDisposable.unsubscribe()
        savePlayingQueueDisposable = Single.fromCallable { songList.toList() }
                .flattenAsObservable { it }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { UpdatePlayingQueueUseCaseRequest(it.mediaId, it.id, it.idInPlaylist) }
                .toList()
                .flatMapCompletable { updatePlayingQueueUseCase.execute(it) }
                .subscribe({}, Throwable::printStackTrace)

    }

    fun updateCurrentSongPosition(list: List<MediaEntity>, position: Int){
        val copy = list.toList()

        val safePosition = ensurePosition(copy, position)
        val idInPlaylist = copy[safePosition].idInPlaylist
        currentSongPosition = safePosition
        currentSongIdUseCase.set(idInPlaylist)

        queueMediaSession.onNext(
                copy.drop(safePosition + 1).take(51).toList())
    }

    @CheckResult
    fun getSongById(songId: Long) : MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.id == songId }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, safePosition)

        return media
    }

    @CheckResult
    fun getSongByIdInPlaylist(idInPlaylist: Int): MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.idInPlaylist == idInPlaylist }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, safePosition)

        return media
    }

    @CheckResult
    @MainThread
    fun getNextSong() : MediaEntity {
        assertMainThread()

        if (repeatMode.isRepeatOne()){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition + 1
        if (newPosition > playingQueue.lastIndex) {
            newPosition = if (repeatMode.isRepeatAll()) 0
                            else playingQueue.lastIndex
        }

        val safePosition = ensurePosition(playingQueue, newPosition)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, newPosition)
        return media
    }

    @CheckResult
    @MainThread
    fun getPreviousSong(playerBookmark: Long) : MediaEntity {
        assertMainThread()

        if (repeatMode.isRepeatOne() || playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition - 1
        if (newPosition < 0) {
            newPosition = if (repeatMode.isRepeatAll()) playingQueue.lastIndex else 0
        }

        val safePosition = ensurePosition(playingQueue, newPosition)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, newPosition)
        return media
    }

    @Contract(pure = true)
    @CheckResult
    private fun ensurePosition(list: List<MediaEntity>, position: Int): Int {
        return MathUtils.clamp(position, 0, list.lastIndex)
    }

    @MainThread
    fun shuffle(){
        assertMainThread()

        playingQueue.shuffle()

        val currentIdInPlaylist = currentSongIdUseCase.get()
        val songPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        if (songPosition != 0){
            playingQueue.swap(0, songPosition)
        }

        updateCurrentSongPosition(playingQueue, 0)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    @MainThread
    fun sort(){
        assertMainThread()

        // todo proper sorting in detail
        playingQueue.sortBy { it.idInPlaylist }

        val currentIdInPlaylist = currentSongIdUseCase.get()
        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        updateCurrentSongPosition(playingQueue, newPosition)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    @MainThread
    fun handleSwap(from: Int, to: Int) {
        assertMainThread()

        playingQueue.swap(from, to)

        val currentInIdPlaylist = currentSongIdUseCase.get() //id remains the same
        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentInIdPlaylist }
        updateCurrentSongPosition(playingQueue, newPosition)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    @MainThread
    fun handleSwapRelative(from: Int, to: Int) {
        handleSwap(from + currentSongPosition + 1, to + currentSongPosition + 1)
    }

    @MainThread
    fun handleRemove(position: Int) {
        assertMainThread()
        if (position >= 0 || position < playingQueue.size){
            // todo case removing current
            // todo case only one song
            playingQueue.removeAt(position)
            persist(playingQueue)
        }

    }

    @MainThread
    fun handleRemoveRelative(position: Int) {
        handleRemove(position + currentSongPosition + 1)
    }

    fun computePositionInQueue(list: List<MediaEntity>, position: Int): PositionInQueue {
        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            position == 0 && position == list.lastIndex -> PositionInQueue.BOTH
            position == 0 -> PositionInQueue.FIRST
            position == list.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }

    fun currentPositionInQueue(): PositionInQueue{
        return computePositionInQueue(playingQueue, currentSongPosition)
    }

}