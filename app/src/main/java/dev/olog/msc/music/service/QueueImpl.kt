package dev.olog.msc.music.service

import android.annotation.SuppressLint
import android.support.annotation.CheckResult
import android.support.annotation.MainThread
import dev.olog.msc.constants.PlaylistConstants.MINI_QUEUE_SIZE
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.msc.domain.interactor.playing.queue.UpdatePlayingQueueUseCase
import dev.olog.msc.domain.interactor.playing.queue.UpdatePlayingQueueUseCaseRequest
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.music.service.model.PositionInQueue
import dev.olog.msc.music.service.model.toMediaEntity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.assertMainThread
import dev.olog.msc.utils.k.extension.clamp
import dev.olog.msc.utils.k.extension.swap
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject

private const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

class QueueImpl @Inject constructor(
        private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
        private val repeatMode: RepeatMode,
        private val musicPreferencesUseCase: MusicPreferencesUseCase,
        private val queueMediaSession: MediaSessionQueue,
        private val getSongUseCase: GetSongUseCase,
        private val mediaSessionDescription: MediaSessionDescription,
        private val enhancedShuffle: EnhancedShuffle
) {

    private var savePlayingQueueDisposable: Disposable? = null

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition = -1

    @MainThread
    fun updatePlayingQueueAndPersist(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private fun persist(songList: List<MediaEntity>) {
        mediaSessionDescription.update(songList)

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

    fun updateCurrentSongPosition(list: List<MediaEntity>, position: Int, immediate: Boolean = false){
        val copy = list.toList()

        val safePosition = ensurePosition(copy, position)
        val idInPlaylist = copy[safePosition].idInPlaylist
        currentSongPosition = safePosition
        musicPreferencesUseCase.setLastIdInPlaylist(idInPlaylist)

        var miniQueue = copy.drop(safePosition + 1).take(MINI_QUEUE_SIZE).toMutableList()
        miniQueue = handleQueueOnRepeatMode(miniQueue, copy[safePosition])

        val activeId = playingQueue[currentSongPosition].idInPlaylist.toLong()
        val model = MediaSessionQueueModel(activeId, miniQueue)

        if (immediate){
            queueMediaSession.onNextImmediate(model)
        } else {
            queueMediaSession.onNext(model)
        }
    }

    @CheckResult
    fun getSongById(idInPlaylist: Long) : MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.idInPlaylist.toLong() == idInPlaylist }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, safePosition, true)

        return media
    }

    @CheckResult
    @MainThread
    fun getNextSong(trackEnded: Boolean) : MediaEntity {
        assertMainThread()

        if (repeatMode.isRepeatOne() && trackEnded){
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

        if (/*repeatMode.isRepeatOne() || */playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD){
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
        return clamp(position, 0, list.lastIndex)
    }

    @MainThread
    fun shuffle(){
        assertMainThread()

        val copy = enhancedShuffle.shuffle(playingQueue)
        playingQueue.clear()
        playingQueue.addAll(copy)

        val currentIdInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val songPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        if (songPosition != 0){
            playingQueue.swap(0, songPosition)
        }

        updateCurrentSongPosition(playingQueue, 0, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    @MainThread
    fun sort(){
        assertMainThread()

        // todo proper sorting in detail
        playingQueue.sortBy { it.idInPlaylist }

        val currentIdInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        updateCurrentSongPosition(playingQueue, newPosition, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    @MainThread
    fun onRepeatModeChanged(){
        assertMainThread()
        var list = playingQueue.drop(currentSongPosition + 1).take(MINI_QUEUE_SIZE).toMutableList()
        list = handleQueueOnRepeatMode(list, playingQueue[currentSongPosition])

        val activeId = playingQueue[currentSongPosition].idInPlaylist.toLong()
        queueMediaSession.onNext(MediaSessionQueueModel(activeId, list))
    }

    @CheckResult
    private fun handleQueueOnRepeatMode(list: MutableList<MediaEntity>, current: MediaEntity)
            : MutableList<MediaEntity>{

        val copy = list.toMutableList()

        if (copy.size < MINI_QUEUE_SIZE && repeatMode.isRepeatAll()){
            while (copy.size <= MINI_QUEUE_SIZE){
                // add all list for n times
                copy.addAll(playingQueue.take(MINI_QUEUE_SIZE))
            }
            return copy.take(MINI_QUEUE_SIZE).toMutableList()
        }
        return copy
    }

    @MainThread
    fun handleSwap(from: Int, to: Int) {
        assertMainThread()

        if (from !in 0..playingQueue.lastIndex || to !in 0..playingQueue.lastIndex){
            return
        }

        playingQueue.swap(from, to)

        val currentInIdPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
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

        if (position !in 0..playingQueue.lastIndex){
            return
        }

        if (position >= 0 || position < playingQueue.size){
            // todo case only one song

            playingQueue.removeAt(position)
            if (position <= currentSongPosition){
                currentSongPosition--
            }
            persist(playingQueue)
        }

    }

    @MainThread
    fun handleRemoveRelative(position: Int) {
        val realPosition = position + currentSongPosition + 1
        handleRemove(realPosition)
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

    @SuppressLint("RxLeakedSubscription")
    fun playLater(songIds: List<Long>) {
        var maxProgressive = playingQueue.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
        maxProgressive += 1

        Single.just(songIds)
                .observeOn(Schedulers.computation())
                .flattenAsObservable { it }
                .flatMapMaybe { getSongUseCase.execute(MediaId.songId(it)).firstElement() }
                .map { it.toMediaEntity(maxProgressive++, MediaId.songId(it.id)) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val copy = playingQueue.toMutableList()
                    copy.addAll(it)
                    updatePlayingQueueAndPersist(copy)
                    onRepeatModeChanged() // not really but updates mini queue
                }, Throwable::printStackTrace)
    }

    @SuppressLint("RxLeakedSubscription")
    fun playNext(songIds: List<Long>) {
        val before = playingQueue.take(currentSongPosition + 1)
        val after = playingQueue.drop(currentSongPosition + 1)

        Single.just(songIds)
                .observeOn(Schedulers.computation())
                .flattenAsObservable { it }
                .flatMapMaybe { getSongUseCase.execute(MediaId.songId(it)).firstElement() }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var currentProgressive = before.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
                    val listToAdd = it.map { it.toMediaEntity(currentProgressive++, MediaId.songId(it.id)) }
                    val afterListUpdated = after.map { it.copy(idInPlaylist = currentProgressive++) }

                    val copy = before.plus(listToAdd).plus(afterListUpdated)
                    updatePlayingQueueAndPersist(copy)
                    onRepeatModeChanged() // not really but updates mini queue
                }, Throwable::printStackTrace)
    }


}