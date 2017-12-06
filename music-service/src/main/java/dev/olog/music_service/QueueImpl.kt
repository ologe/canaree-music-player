package dev.olog.music_service

import android.support.annotation.CheckResult
import android.support.v4.math.MathUtils
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.domain.interactor.music_service.CurrentSongIdUseCase
import dev.olog.domain.interactor.music_service.UpdateMiniQueueUseCase
import dev.olog.domain.interactor.music_service.UpdatePlayingQueueUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.music_service.model.MediaEntity
import dev.olog.music_service.model.toMediaEntity
import dev.olog.shared.swap
import dev.olog.shared.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject

class QueueImpl @Inject constructor(
        private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
        private val updateMiniQueueUseCase: UpdateMiniQueueUseCase,
        private val repeatMode: RepeatMode,
        private val currentSongIdUseCase: CurrentSongIdUseCase,
        private val getAllSongsUseCase: GetAllSongsUseCase
) {

    companion object {
        private const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000
    }

    private var savePlayingQueueDisposable: Disposable? = null

    val playingQueue = Vector<MediaEntity>()

    var currentSongPosition = -1

    fun updatePlayingQueue(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)
    }

    fun updatePlayingQueueAndPersist(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private fun persist(songList: List<MediaEntity>) {
        savePlayingQueueDisposable.unsubscribe()
        savePlayingQueueDisposable = songList.toFlowable()
                .observeOn(Schedulers.io())
                .map { it.id }
                .toList()
                .flatMapCompletable { updatePlayingQueueUseCase.execute(it) }
                .subscribe({}, Throwable::printStackTrace)
    }

    fun updateCurrentSongPosition(list: List<MediaEntity>, position: Int){
        val pos = ensurePosition(list, position)
        val songId = list[pos].id
        currentSongPosition = pos
        currentSongIdUseCase.set(songId)

        val miniQueue = list.asSequence().drop(currentSongPosition + 1).take(51)
                .map { it.id }.toList()
        updateMiniQueueUseCase.execute(miniQueue)
    }

    fun getSongById(songId: Long) : MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.id == songId }
        val position = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[position]
        updateCurrentSongPosition(playingQueue, position)

        return media
    }

    fun getNextSong() : MediaEntity {
        if (repeatMode.isRepeatOne()){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition + 1
        if (newPosition > playingQueue.lastIndex) {
            newPosition = if (repeatMode.isRepeatAll()) 0 else playingQueue.lastIndex
        }

        val media = playingQueue[newPosition]
        updateCurrentSongPosition(playingQueue, newPosition)
        return media
    }

    fun getPreviousSong(playerBookmark: Long) : MediaEntity {
        if (repeatMode.isRepeatOne() || playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition - 1
        if (newPosition < 0) {
            newPosition = if (repeatMode.isRepeatAll()) playingQueue.lastIndex else 0
        }

        val media = playingQueue[newPosition]
        updateCurrentSongPosition(playingQueue, newPosition)
        return media
    }

    fun addItemToQueue(item: MediaDescriptionCompat) {
        val split = item.mediaId!!.split(",")
        split.toFlowable()
                .observeOn(Schedulers.computation())
                .map { it.toLong() }
                .flatMapMaybe { songId -> getAllSongsUseCase.execute()
                        .flatMapIterable { it }
                        .filter { it.id == songId }
                        .map { it.toMediaEntity() }
                        .firstElement()
                }.toList()
                .doOnSuccess { playingQueue.addAll(it) }
                .doOnSuccess { persist(it) }
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

}