package dev.olog.service.music.queue

import androidx.annotation.CheckResult
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCase
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.toMediaEntity
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.shared.CustomScope
import dev.olog.shared.android.utils.assertBackgroundThread
import dev.olog.shared.android.utils.assertMainThread
import dev.olog.shared.clamp
import dev.olog.shared.swap
import kotlinx.coroutines.*
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject

const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

internal class QueueImpl @Inject constructor(
    lifecycleOwner: LifecycleOwner,
    private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
    private val repeatMode: MusicServiceRepeatMode,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val queueMediaSession: MediaSessionQueue,
    private val enhancedShuffle: EnhancedShuffle,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : DefaultLifecycleObserver,
    CoroutineScope by CustomScope() {

    private var savePlayingQueueJob: Job? = null

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition = -1

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        persist(playingQueue)
        playingQueue.getOrNull(currentSongPosition)?.let {
            musicPreferencesUseCase.setLastIdInPlaylist(it.idInPlaylist)
        }
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
        savePlayingQueueJob?.cancel()
        savePlayingQueueJob = launch {
            assertBackgroundThread()

            val request = songList.map {
                UpdatePlayingQueueUseCaseRequest(
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

    suspend fun playLater(songIds: List<Long>, isPodcast: Boolean) {
        assertBackgroundThread()

        if (isEmpty()){
            return
        }

        val queue = playingQueue.toList() // work on a copy

        var maxIdInPlaylist = queue.maxBy { it.idInPlaylist }?.idInPlaylist ?: 0

        val songList: List<MediaEntity> = songIds.mapNotNull { id ->
            val track: Song? = if (isPodcast) {
                podcastGateway.getByParam(id)
            } else {
                songGateway.getByParam(id)
            }
            track
        }.map { song -> song.toMediaEntity(++maxIdInPlaylist, song.getMediaId()) }

        val newQueue = queue + songList

        updateState(newQueue, currentSongPosition, false, true)


        withContext(Dispatchers.Main) {
            onRepeatModeChanged() // not really but updates mini queue
        }
    }

    suspend fun playNext(songIds: List<Long>, isPodcast: Boolean) {
        assertBackgroundThread()

        if (isEmpty()){
            return
        }

        val queue = playingQueue.toList() // work on a copy

        val before = queue.take(currentSongPosition + 1)
        val after = queue.drop(currentSongPosition + 1)

        var maxIdInPlaylist = queue.maxBy { it.idInPlaylist }?.idInPlaylist ?: 0

        val songList: List<MediaEntity> = songIds.mapNotNull { id ->
            val track: Song? = if (isPodcast) {
                podcastGateway.getByParam(id)
            } else {
                songGateway.getByParam(id)
            }
            track
        }.map { song -> song.toMediaEntity(++maxIdInPlaylist, song.getMediaId()) }

        val newQueue = before + songList + after

        updateState(newQueue, currentSongPosition, false, true)

        withContext(Dispatchers.Main) {
            onRepeatModeChanged() // not really but updates mini queue
        }
    }


}