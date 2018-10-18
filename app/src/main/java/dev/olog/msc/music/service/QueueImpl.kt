package dev.olog.msc.music.service

import android.annotation.SuppressLint
import androidx.annotation.CheckResult
import androidx.annotation.MainThread
import dev.olog.msc.constants.PlaylistConstants.MINI_QUEUE_SIZE
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.item.GetPodcastUseCase
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
import kotlin.properties.Delegates

private const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

class QueueImpl @Inject constructor(
        private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
        private val repeatMode: RepeatMode,
        private val musicPreferencesUseCase: MusicPreferencesUseCase,
        private val queueMediaSession: MediaSessionQueue,
        private val getSongUseCase: GetSongUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val enhancedShuffle: EnhancedShuffle
) {

    private var savePlayingQueueDisposable: Disposable? = null

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition by Delegates.observable(-1) { _, _, new ->
        musicPreferencesUseCase.setLastPositionInQueue(new)
    }

    @MainThread
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

    fun updateCurrentSongPosition(list: List<MediaEntity>, position: Int, immediate: Boolean = false){
        val copy = list.toList()

        val safePosition = ensurePosition(copy, position)
        val idInPlaylist = copy[safePosition].idInPlaylist
        currentSongPosition = safePosition
        musicPreferencesUseCase.setLastIdInPlaylist(idInPlaylist)

        var miniQueue = copy.asSequence()
                .drop(safePosition + 1)
                .take(MINI_QUEUE_SIZE)
                .toMutableList()
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
    fun getCurrentSong(): MediaEntity? {
        return playingQueue.getOrNull(currentSongPosition)
    }

    @CheckResult
    @MainThread
    fun getNextSong(trackEnded: Boolean) : MediaEntity? {
        assertMainThread()

        if (repeatMode.isRepeatOne() && trackEnded){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition + 1
        if (newPosition > playingQueue.lastIndex && repeatMode.isRepeatAll()) {
            newPosition = 0
        }

        if (isPositionValid(playingQueue, newPosition)){
            val media = playingQueue[newPosition]
            updateCurrentSongPosition(playingQueue, newPosition)
            return media
        }
        return null
    }

    @CheckResult
    @MainThread
    fun getPreviousSong(playerBookmark: Long) : MediaEntity? {
        assertMainThread()

        if (/*repeatMode.isRepeatOne() || */playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD){
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition - 1

        if (currentSongPosition == 0 && newPosition < 0 && !repeatMode.isRepeatAll()){
            // restart song from beginning if is first
            return playingQueue[currentSongPosition]
        }

        if (newPosition < 0 && repeatMode.isRepeatAll()) {
            newPosition = playingQueue.lastIndex
        }

        if (isPositionValid(playingQueue, newPosition)){
            val media = playingQueue[newPosition]
            updateCurrentSongPosition(playingQueue, newPosition)
            return media
        }
        return null
    }

    @Contract(pure = true)
    @CheckResult
    private fun ensurePosition(list: List<MediaEntity>, position: Int): Int {
        return clamp(position, 0, list.lastIndex)
    }

    @Contract(pure = true)
    @CheckResult
    private fun isPositionValid(list: List<MediaEntity>, position: Int): Boolean{
        return position in 0 .. list.lastIndex
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
    fun handleRemove(position: Int): Boolean {
        assertMainThread()

        if (position !in 0..playingQueue.lastIndex){
            return false
        }

        if (position >= 0 || position < playingQueue.size){
            // todo case only one song

            playingQueue.removeAt(position)
            if (position <= currentSongPosition){
                currentSongPosition--
            }
            persist(playingQueue)
        }
        return playingQueue.isEmpty()
    }

    @MainThread
    fun handleRemoveRelative(position: Int): Boolean {
        val realPosition = position + currentSongPosition + 1
        return handleRemove(realPosition)
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

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    fun playLater(songIds: List<Long>, isPodcast: Boolean) {
        var maxProgressive = playingQueue.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
        maxProgressive += 1

        Single.just(songIds)
                .observeOn(Schedulers.computation())
                .flattenAsObservable { it }
                .flatMapMaybe {
                    if (isPodcast){
                        getPodcastUseCase.execute(MediaId.podcastId(it)).firstElement()
                                .map { it.toMediaEntity(maxProgressive++, MediaId.songId(it.id)) }
                    } else {
                        getSongUseCase.execute(MediaId.songId(it)).firstElement()
                                .map { it.toMediaEntity(maxProgressive++, MediaId.songId(it.id)) }
                    }

                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val copy = playingQueue.toMutableList()
                    copy.addAll(it)
                    updatePlayingQueueAndPersist(copy)
                    onRepeatModeChanged() // not really but updates mini queue
                }, Throwable::printStackTrace)
    }

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    fun playNext(songIds: List<Long>, isPodcast: Boolean) {
        val before = playingQueue.take(currentSongPosition + 1)
        val after = playingQueue.drop(currentSongPosition + 1)

        Single.just(songIds)
                .observeOn(Schedulers.computation())
                .flattenAsObservable { it }
                .flatMapMaybe {
                    if (isPodcast){
                        getPodcastUseCase.execute(MediaId.podcastId(it)).firstElement()
                    } else {
                        getSongUseCase.execute(MediaId.songId(it)).firstElement()
                    }

                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var currentProgressive = before.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
                    val listToAdd = it.map {
                        when (it){
                            is Song -> it.toMediaEntity(currentProgressive++, MediaId.songId(it.id))
                            is Podcast -> it.toMediaEntity(currentProgressive++, MediaId.podcastId(it.id))
                            else -> throw IllegalArgumentException("nor song nor podcast")
                        }
                    }
                    val afterListUpdated = after.map { it.copy(idInPlaylist = currentProgressive++) }

                    val copy = before.plus(listToAdd).plus(afterListUpdated)
                    updatePlayingQueueAndPersist(copy)
                    onRepeatModeChanged() // not really but updates mini queue
                }, Throwable::printStackTrace)
    }

//    fun moveToPlayNext(idInPlaylist: Int) {
//        assertMainThread()
//        var copy = playingQueue.toMutableList()
//
//        val indexOf = copy
//                .asSequence()
//                .take(50)
//                .indexOfFirst { it.idInPlaylist == idInPlaylist }
//
//        val item = copy[indexOf]
//        copy.removeAt(indexOf)
//        copy.add(currentSongPosition + 1, item)
//
//        copy = copy.mapIndexed { index, mediaEntity -> mediaEntity.copy(idInPlaylist = index) }
//                .toMutableList()
//
//        // updating mini queue
//        var list = copy.drop(currentSongPosition + 1).take(MINI_QUEUE_SIZE).toMutableList()
//        list = handleQueueOnRepeatMode(list, copy[currentSongPosition])
//
//        val activeId = copy[currentSongPosition].idInPlaylist.toLong()
//        queueMediaSession.onNext(MediaSessionQueueModel(activeId, list))
//
//        updatePlayingQueueAndPersist(copy)
//    }


}