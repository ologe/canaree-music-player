package dev.olog.service.music.queue

import androidx.annotation.CheckResult
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.UpdatePlayingQueueUseCase
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.toMediaEntity
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.shared.CustomScope
import dev.olog.shared.android.utils.assertBackgroundThread
import dev.olog.shared.android.utils.assertMainThread
import dev.olog.shared.autoDisposeJob
import dev.olog.core.clamp
import dev.olog.core.swap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject

const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

internal class QueueImpl @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
    private val repeatMode: MusicServiceRepeatMode,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val queueMediaSession: MediaSessionQueue,
    private val enhancedShuffle: EnhancedShuffle,
    private val trackGateway: TrackGateway,
    private val schedulers: Schedulers
) : DefaultLifecycleObserver,
    CoroutineScope by CustomScope(schedulers.cpu) {

    private var savePlayingQueueJob by autoDisposeJob()

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition = -1

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        persist(playingQueue)
        playingQueue.getOrNull(currentSongPosition)?.let {
            musicPreferencesUseCase.setLastIdInPlaylist(it.idInPlaylist)
        }
//        cancel() TODO mmm cancelling will not persist queue, is needed persist here??
    }

    internal fun isEmpty() = playingQueue.isEmpty()

    /**
     * @param persist when true a new queue must be selected, queue and index will be persisted
     *  to storage
     */
    fun updateState(
        songList: List<MediaEntity>,
        index: Int,
        updateImmediate: Boolean,
        persist: Boolean
    ) {
        updatePlayingQueue(songList)
        updateCurrentSongPosition(index)
        publishMiniQueue(songList, index, updateImmediate)
        if (persist) {
            persist(songList)
            songList.getOrNull(index)?.let {
                musicPreferencesUseCase.setLastIdInPlaylist(it.idInPlaylist)
            }
        }
    }

    private fun updatePlayingQueue(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)
    }

    private fun updateCurrentSongPosition(position: Int) {
        val safePosition = ensurePosition(playingQueue, position)
        currentSongPosition = safePosition
    }

    private fun persist(songList: List<MediaEntity>) {
        savePlayingQueueJob = launch {
            assertBackgroundThread()

            val request = songList.map {
                UpdatePlayingQueueUseCase.Request(
                    it.mediaId,
                    it.id,
                    it.idInPlaylist
                )
            }
            yield()
            updatePlayingQueueUseCase(request)
        }
    }

    @CheckResult
    fun getCurrentSong(): MediaEntity? {
        if (isEmpty()){
            return null
        }
        return playingQueue.getOrNull(currentSongPosition)
    }

    private fun publishMiniQueue(
        list: List<MediaEntity>,
        currentPosition: Int,
        immediate: Boolean
    ) {
        launch {
            assertBackgroundThread()

            val safePosition = ensurePosition(list, currentPosition)
            val miniQueue = list.asSequence()
                .drop(safePosition + 1)
                .take(PlayingQueueGateway.MINI_QUEUE_SIZE)
                .toMutableList()
                .handleQueueOnRepeatMode()

            if (immediate) {
                queueMediaSession.onNextImmediate(miniQueue)
            } else {
                queueMediaSession.onNext(miniQueue)
            }
        }
    }

    @CheckResult
    fun getSongById(idInPlaylist: Int): MediaEntity? {
        assertMainThread()

        if (isEmpty()){
            return null
        }

        val positionInQueue = playingQueue.indexOfFirst { it.idInPlaylist == idInPlaylist }
        if (positionInQueue == -1){
            return null
        }
        publishMiniQueue(playingQueue, positionInQueue, true)
        updateCurrentSongPosition(positionInQueue)
        musicPreferencesUseCase.setLastIdInPlaylist(playingQueue[positionInQueue].idInPlaylist)
        return playingQueue[currentSongPosition]
    }

    @CheckResult
    fun getNextSong(trackEnded: Boolean): MediaEntity? {
        assertMainThread()

        if (isEmpty()){
            return null
        }

        if (repeatMode.isRepeatOne() && trackEnded) {
            return playingQueue.getOrNull(currentSongPosition) ?: return null
        }

        var newPosition = currentSongPosition + 1
        if (newPosition > playingQueue.lastIndex && repeatMode.isRepeatAll()) {
            newPosition = 0
        }

        if (isPositionValid(playingQueue, newPosition)) {
            val media = playingQueue[newPosition]
            publishMiniQueue(playingQueue.toList(), newPosition, false)
            updateCurrentSongPosition(newPosition)
            musicPreferencesUseCase.setLastIdInPlaylist(playingQueue[newPosition].idInPlaylist)
            return media
        }
        return null
    }

    @CheckResult
    fun getPreviousSong(playerBookmark: Long): MediaEntity? {
        assertMainThread()

        if (isEmpty()){
            return null
        }

        val isPodcast = getCurrentSong()?.isPodcast ?: false

        if (!isPodcast && playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD) {
            return playingQueue.getOrNull(currentSongPosition) ?: return null
        }

        var newPosition = currentSongPosition - 1

        if (currentSongPosition == 0 && newPosition < 0 && !repeatMode.isRepeatAll()) {
            // restart song from beginning if is first
            return playingQueue.getOrNull(currentSongPosition) ?: return null
        }

        if (newPosition < 0 && repeatMode.isRepeatAll()) {
            newPosition = playingQueue.lastIndex
        }

        if (isPositionValid(playingQueue, newPosition)) {
            val media = playingQueue[newPosition]
            publishMiniQueue(playingQueue.toList(), newPosition, false)
            updateCurrentSongPosition(newPosition)
            musicPreferencesUseCase.setLastIdInPlaylist(playingQueue[newPosition].idInPlaylist)
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
    private fun isPositionValid(list: List<MediaEntity>, position: Int): Boolean {
        return position in 0..list.lastIndex
    }

    fun shuffle() {
        assertMainThread()

        if (isEmpty()){
            return
        }

        val currentPlaying = playingQueue.getOrNull(currentSongPosition) ?: return

        val queue = enhancedShuffle.shuffle(playingQueue)
        updatePlayingQueue(queue)

        val songPosition =
            playingQueue.indexOfFirst { it.idInPlaylist == currentPlaying.idInPlaylist }
        if (songPosition != 0) {
            playingQueue.swap(0, songPosition)
        }

        updateCurrentSongPosition(0)
        publishMiniQueue(playingQueue.toList(), 0, true)
        // todo check if current song is first/last ecc and update ui
    }

    fun sort() {
        assertMainThread()

        if (isEmpty()){
            return
        }

        val currentPlaying = playingQueue.getOrNull(currentSongPosition) ?: return

        playingQueue.sortBy { it.idInPlaylist }

        val newPosition =
            playingQueue.indexOfFirst { it.idInPlaylist == currentPlaying.idInPlaylist }
        updateCurrentSongPosition(newPosition)
        publishMiniQueue(playingQueue.toList(), newPosition, true)
        // todo check if current song is first/last ecc and update ui
    }

    fun onRepeatModeChanged() {
        assertMainThread()
        if (isEmpty()){
            return
        }

        currentSongPosition = ensurePosition(playingQueue, currentSongPosition)
        val list = playingQueue.drop(currentSongPosition + 1)
            .take(PlayingQueueGateway.MINI_QUEUE_SIZE)
            .toMutableList()
            .handleQueueOnRepeatMode()

        queueMediaSession.onNext(list)
    }

    @CheckResult
    private fun MutableList<MediaEntity>.handleQueueOnRepeatMode(): MutableList<MediaEntity> {
        val copy = this.toMutableList()

        if (copy.size < PlayingQueueGateway.MINI_QUEUE_SIZE && repeatMode.isRepeatAll()) {
            // repeat all, show another copy of the list ah the end
            copy.addAll(playingQueue.take(PlayingQueueGateway.MINI_QUEUE_SIZE))
            return copy.asSequence().take(PlayingQueueGateway.MINI_QUEUE_SIZE).toMutableList()
        }
        return copy
    }

    fun handleSwap(from: Int, to: Int) {
        assertMainThread()

        if (isEmpty()){
            return
        }

        if (from !in 0..playingQueue.lastIndex || to !in 0..playingQueue.lastIndex) {
            return
        }

        val currentPlaying = playingQueue.getOrNull(currentSongPosition) ?: return

        playingQueue.swap(from, to)

        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentPlaying.idInPlaylist }

        updateCurrentSongPosition(newPosition)
        // todo check if current song is first/last ecc and update ui
        publishMiniQueue(playingQueue, newPosition, false)
    }

    fun handleSwapRelative(from: Int, to: Int) {
        handleSwap(from + currentSongPosition + 1, to + currentSongPosition + 1)
    }

    /**
     * moves the item so it can be played after current song
     */
    fun handleMoveRelative(position: Int) {
        assertMainThread()

        if (isEmpty()){
            return
        }

        if (position !in 0..playingQueue.lastIndex) {
            return
        }
        val item = playingQueue.removeAt(position + currentSongPosition + 1)
        playingQueue.add(currentSongPosition + 1, item)
        persist(playingQueue)
        publishMiniQueue(playingQueue, currentSongPosition, true)
    }

    fun handleRemove(position: Int) {
        assertMainThread()

        if (isEmpty()){
            return
        }

        if (position !in 0..playingQueue.lastIndex) {
            return
        }

        playingQueue.removeAt(position)
        if (position <= currentSongPosition) {
            currentSongPosition--
        }
        publishMiniQueue(playingQueue.toList(), currentSongPosition, false)
    }

    fun handleRemoveRelative(position: Int) {
        handleRemove(position + currentSongPosition + 1)
    }

    fun computePositionInQueue(
        list: List<MediaEntity>,
        position: Int
    ): PositionInQueue {
        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            position == 0 && position == list.lastIndex -> PositionInQueue.FIRST_AND_LAST
            position == 0 -> PositionInQueue.FIRST
            position == list.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }

    fun currentPositionInQueue(): PositionInQueue {
        return computePositionInQueue(playingQueue, currentSongPosition)
    }

    suspend fun playLater(songIds: List<Long>) {
        assertBackgroundThread()

        if (isEmpty()){
            return
        }

        val queue = playingQueue.toList() // work on a copy

        var maxIdInPlaylist = queue.maxBy { it.idInPlaylist }?.idInPlaylist ?: 0

        val songList: List<MediaEntity> = songIds
            .mapNotNull { id -> trackGateway.getByParam(id) }
            .map { song -> song.toMediaEntity(++maxIdInPlaylist, song.parentMediaId) }

        val newQueue = queue + songList

        updateState(
            songList = newQueue,
            index = currentSongPosition,
            updateImmediate = false,
            persist = true
        )

        withContext(schedulers.main) {
            onRepeatModeChanged() // not really but updates mini queue
        }
    }

    suspend fun playNext(songIds: List<Long>) {
        assertBackgroundThread()

        if (isEmpty()){
            return
        }

        val queue = playingQueue.toList() // work on a copy

        val before = queue.take(currentSongPosition + 1)
        val after = queue.drop(currentSongPosition + 1)

        var maxIdInPlaylist = queue.maxBy { it.idInPlaylist }?.idInPlaylist ?: 0

        val songList: List<MediaEntity> = songIds
            .mapNotNull { id -> trackGateway.getByParam(id) }
            .map { song -> song.toMediaEntity(++maxIdInPlaylist, song.parentMediaId) }

        val newQueue = before + songList + after

        updateState(
            songList = newQueue,
            index = currentSongPosition,
            updateImmediate = false,
            persist = true
        )

        withContext(schedulers.main) {
            onRepeatModeChanged() // not really but updates mini queue
        }
    }


}