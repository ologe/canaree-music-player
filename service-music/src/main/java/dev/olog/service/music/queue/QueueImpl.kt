package dev.olog.service.music.queue

import androidx.annotation.CheckResult
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.entity.track.getMediaId
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
import dev.olog.shared.extensions.swap
import dev.olog.shared.utils.assertBackgroundThread
import dev.olog.shared.utils.assertMainThread
import dev.olog.shared.utils.clamp
import kotlinx.coroutines.*
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

// TODO move
const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

internal class QueueImpl @Inject constructor(
    private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
    private val repeatMode: MusicServiceRepeatMode,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val queueMediaSession: MediaSessionQueue,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val enhancedShuffle: EnhancedShuffle
) : CoroutineScope by CustomScope() {

    private var savePlayingQueueJob: Job? = null

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition by Delegates.observable(-1) { _, _, new ->
        musicPreferencesUseCase.setLastPositionInQueue(new)
    }

    fun updatePlayingQueueAndPersist(songList: List<MediaEntity>) {
        assertMainThread()
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private fun persist(songList: List<MediaEntity>) {
        savePlayingQueueJob?.cancel()
        savePlayingQueueJob = launch {
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

    fun updateCurrentSongPosition(
        list: List<MediaEntity>,
        position: Int
    ) {
        require(list !== playingQueue)

        val safePosition = ensurePosition(list, position)
        currentSongPosition = safePosition
    }

    fun publishMiniQueue(list: List<MediaEntity>, currentPosition: Int, immediate: Boolean){
        require(list !== playingQueue)

        val safePosition = ensurePosition(list, currentPosition)
        var miniQueue = list.asSequence()
            .drop(safePosition + 1)
            .take(PlayingQueueGateway.MINI_QUEUE_SIZE)
            .toMutableList()
        miniQueue = handleQueueOnRepeatMode(miniQueue)

        if (immediate) {
            queueMediaSession.onNextImmediate(miniQueue)
        } else {
            queueMediaSession.onNext(miniQueue)
        }
    }

    @CheckResult
    fun getSongByPosition(idInPlaylist: Long): MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.idInPlaylist.toLong() == idInPlaylist }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue.toList(), safePosition)
        publishMiniQueue(playingQueue.toList(), safePosition, true)

        return media
    }

    @CheckResult
    fun getCurrentSong(): MediaEntity? {
        assertBackgroundThread()
        return playingQueue.getOrNull(currentSongPosition)
    }

    @CheckResult
    fun getNextSong(trackEnded: Boolean): MediaEntity? {
        assertMainThread()

        if (repeatMode.isRepeatOne() && trackEnded) {
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition + 1
        if (newPosition > playingQueue.lastIndex && repeatMode.isRepeatAll()) {
            newPosition = 0
        }

        if (isPositionValid(playingQueue, newPosition)) {
            val media = playingQueue[newPosition]
            updateCurrentSongPosition(playingQueue.toList(), newPosition)
            publishMiniQueue(playingQueue.toList(), newPosition, false)
            return media
        }
        return null
    }

    @CheckResult
    fun getPreviousSong(playerBookmark: Long): MediaEntity? {
        assertMainThread()

        if (/*repeatMode.isRepeatOne() || */playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD) {
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition - 1

        if (currentSongPosition == 0 && newPosition < 0 && !repeatMode.isRepeatAll()) {
            // restart song from beginning if is first
            return playingQueue[currentSongPosition]
        }

        if (newPosition < 0 && repeatMode.isRepeatAll()) {
            newPosition = playingQueue.lastIndex
        }

        if (isPositionValid(playingQueue, newPosition)) {
            val media = playingQueue[newPosition]
            updateCurrentSongPosition(playingQueue.toList(), newPosition)
            publishMiniQueue(playingQueue.toList(), newPosition, false)
            return media
        }
        return null
    }

    @Contract(pure = true)
    @CheckResult
    private fun ensurePosition(
        list: List<MediaEntity>,
        position: Int
    ): Int {
        return clamp(position, 0, list.lastIndex)
    }

    @Contract(pure = true)
    @CheckResult
    private fun isPositionValid(
        list: List<MediaEntity>,
        position: Int
    ): Boolean {
        return position in 0..list.lastIndex
    }

    fun shuffle() {
        assertMainThread()

        val copy = enhancedShuffle.shuffle(playingQueue)
        playingQueue.clear()
        playingQueue.addAll(copy)

        val currentIdInPlaylist = musicPreferencesUseCase.getLastPositionInQueue()
        val songPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        if (songPosition != 0) {
            playingQueue.swap(0, songPosition)
        }

        updateCurrentSongPosition(playingQueue.toList(), 0)
        publishMiniQueue(playingQueue.toList(), 0, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    fun sort() {
        assertMainThread()

        // todo proper sorting in detail
        playingQueue.sortBy { it.idInPlaylist }

        val currentIdInPlaylist = musicPreferencesUseCase.getLastPositionInQueue()
        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        updateCurrentSongPosition(playingQueue.toList(), newPosition)
        publishMiniQueue(playingQueue.toList(), newPosition, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    fun onRepeatModeChanged() {
        assertMainThread()

        currentSongPosition = ensurePosition(playingQueue, currentSongPosition)
        var list =
            playingQueue.drop(currentSongPosition + 1).take(PlayingQueueGateway.MINI_QUEUE_SIZE)
                .toMutableList()
        list = handleQueueOnRepeatMode(list)

        queueMediaSession.onNext(list)
    }

    @CheckResult
    private fun handleQueueOnRepeatMode(list: MutableList<MediaEntity>)
            : MutableList<MediaEntity> {

        val copy = list.toMutableList()

        if (copy.size < PlayingQueueGateway.MINI_QUEUE_SIZE && repeatMode.isRepeatAll()) {
            while (copy.size <= PlayingQueueGateway.MINI_QUEUE_SIZE) {
                // add all list for n times
                copy.addAll(playingQueue.take(PlayingQueueGateway.MINI_QUEUE_SIZE))
            }
            return copy.asSequence().take(PlayingQueueGateway.MINI_QUEUE_SIZE).toMutableList()
        }
        return copy
    }

    fun handleSwap(from: Int, to: Int) {
        assertMainThread()

        if (from !in 0..playingQueue.lastIndex || to !in 0..playingQueue.lastIndex) {
            return
        }

        val current = playingQueue[currentSongPosition]

        playingQueue.swap(from, to)

        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == current.idInPlaylist }

        val queue = playingQueue.toMutableList()
        playingQueue.clear()
        playingQueue.addAll(queue.mapIndexed { index, mediaEntity -> mediaEntity.copy(idInPlaylist = index) })

        updateCurrentSongPosition(playingQueue.toList(), newPosition)
        // todo check if current song is first/last ecc and update ui

        launch {
            delay(2000)
            publishMiniQueue(playingQueue.toList(), newPosition, true)
            persist(playingQueue)
        }
    }

    fun handleSwapRelative(from: Int, to: Int) {
        handleSwap(from + currentSongPosition + 1, to + currentSongPosition + 1)
    }

    fun handleRemove(position: Int) {
        assertMainThread()

        if (position !in 0..playingQueue.lastIndex) {
            return
        }

        // todo case only one song

        playingQueue.removeAt(position)
        if (position <= currentSongPosition) {
            currentSongPosition--
        }
        persist(playingQueue)
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

        val queue = playingQueue.toList() // work on a copy

        val songList : List<MediaEntity> = songIds.mapNotNull {
            val track: Song? = if (isPodcast) {
                val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS, it.toString())
                podcastGateway.getByParam(mediaId.categoryId)
            } else {
                val mediaId = MediaId.createCategoryValue(MediaIdCategory.SONGS, it.toString())
                songGateway.getByParam(mediaId.categoryId)
            }
            track
        }.mapIndexed { index, song -> song.toMediaEntity(index, song.getMediaId()) }

        val newQueue = (queue + songList).mapIndexed { index, mediaEntity ->
            mediaEntity.copy(idInPlaylist = index)
        }

        updatePlayingQueueAndPersist(newQueue)

        withContext(Dispatchers.Main){
            onRepeatModeChanged() // not really but updates mini queue
        }
    }

    suspend fun playNext(songIds: List<Long>, isPodcast: Boolean) {
        // TODO not working
        assertBackgroundThread()
        val queue = playingQueue.toList() // work on a copy

        // | 0 | 1 | 2 | 3 | 4 | 5 |
        val before = queue.take(currentSongPosition)
        val after = queue.drop(currentSongPosition + 1)

        val songList : List<MediaEntity> = songIds.mapNotNull {
            val track: Song? = if (isPodcast) {
                val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS, it.toString())
                podcastGateway.getByParam(mediaId.categoryId)
            } else {
                val mediaId = MediaId.createCategoryValue(MediaIdCategory.SONGS, it.toString())
                songGateway.getByParam(mediaId.categoryId)
            }
            track
        }.mapIndexed { index, song -> song.toMediaEntity(index, song.getMediaId()) }

        val newQueue = (before + songList + after).mapIndexed { index, mediaEntity ->
            mediaEntity.copy(idInPlaylist = index)
        }

        updatePlayingQueueAndPersist(newQueue)

        withContext(Dispatchers.Main){
            onRepeatModeChanged() // not really but updates mini queue
        }
    }


}