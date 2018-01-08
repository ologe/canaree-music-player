package dev.olog.music_service

import android.support.annotation.CheckResult
import android.support.v4.math.MathUtils
import dev.olog.domain.interactor.music_service.CurrentIdInPlaylistUseCase
import dev.olog.domain.interactor.music_service.UpdatePlayingQueueUseCase
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.shuffle
import dev.olog.shared.swap
import dev.olog.shared.unsubscribe
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

    val playingQueue = Vector<MediaEntity>()

    var currentSongPosition = -1

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
                .map { it.mediaId to it.id }
                .toList()
                .flatMapCompletable { updatePlayingQueueUseCase.execute(it) }
                .subscribe({}, Throwable::printStackTrace)

    }

    fun updateCurrentSongPosition(list: List<MediaEntity>, position: Int){
        val safePosition = ensurePosition(list, position)
        val idInPlaylist = list[safePosition].idInPlaylist
        currentSongPosition = safePosition
        currentSongIdUseCase.set(idInPlaylist)

        queueMediaSession.onNext(
                list.drop(safePosition + 1).take(51).toList())
    }

    fun getSongById(songId: Long) : MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.id == songId }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, safePosition)

        return media
    }

    fun getSongByIdInPlaylist(idInPlaylist: Int): MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.idInPlaylist == idInPlaylist }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, safePosition)

        return media
    }

    fun getNextSong() : MediaEntity {
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

    fun getPreviousSong(playerBookmark: Long) : MediaEntity {
        if (repeatMode.isRepeatOne() || playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition - 1
        if (newPosition < 0) {
            newPosition = if (repeatMode.isRepeatAll()) playingQueue.lastIndex
                            else 0
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

    fun shuffle(){
        val item = playingQueue[currentSongPosition]

        playingQueue.shuffle()

        val songPosition = playingQueue.indexOf(item)
        if (songPosition != 0){
            playingQueue.swap(0, songPosition)
        }

        currentSongPosition = 0
        updateCurrentSongPosition(playingQueue, currentSongPosition)
        persist(playingQueue)
    }

    fun sort(){
        val playingSong = playingQueue[currentSongPosition]
        // todo proper sorting in detail
        playingQueue.sortBy { it.title }

        currentSongPosition = playingQueue.indexOf(playingSong)
        updateCurrentSongPosition(playingQueue, currentSongPosition)
        persist(playingQueue)
    }

    fun handleSwap(from: Int, to: Int) {
        playingQueue.swap(from, to)
        persist(playingQueue)
        val currentInPlaylist = currentSongIdUseCase.get()
        updateCurrentSongPosition(playingQueue, playingQueue
                .indexOfFirst { it.idInPlaylist == currentInPlaylist }
        )
        // todo check if current song is first/last ecc and update ui
    }

    fun handleSwapRelative(from: Int, to: Int) {
        handleSwap(from + currentSongPosition + 1, to + currentSongPosition + 1)
    }
}