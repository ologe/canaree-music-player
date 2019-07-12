package dev.olog.service.music.queue

import android.annotation.SuppressLint
import androidx.annotation.CheckResult
import androidx.annotation.MainThread
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
import dev.olog.service.music.model.toMediaEntity
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.shared.CustomScope
import dev.olog.shared.extensions.swap
import dev.olog.shared.utils.assertMainThread
import dev.olog.shared.utils.clamp
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

// TODO move
const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

class QueueImpl @Inject constructor(
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

    @MainThread
    fun updatePlayingQueueAndPersist(songList: List<MediaEntity>) {
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private fun persist(songList: List<MediaEntity>) {
        savePlayingQueueJob?.cancel()
        savePlayingQueueJob = launch {
            val request = songList.map { UpdatePlayingQueueUseCaseRequest(it.mediaId, it.id, it.idInPlaylist) }
            yield()
            updatePlayingQueueUseCase(request)
        }
    }

    fun updateCurrentSongPosition(
        list: List<MediaEntity>,
        position: Int,
        immediate: Boolean = false
    ) {
        val copy = list.toList()

        val safePosition = ensurePosition(copy, position)
        val idInPlaylist = copy[safePosition].idInPlaylist
        currentSongPosition = safePosition
        musicPreferencesUseCase.setLastIdInPlaylist(idInPlaylist)

        var miniQueue = copy.asSequence()
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
            updateCurrentSongPosition(playingQueue, newPosition)
            return media
        }
        return null
    }

    @CheckResult
    @MainThread
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
            updateCurrentSongPosition(playingQueue, newPosition)
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

    @MainThread
    fun shuffle() {
        assertMainThread()

        val copy = enhancedShuffle.shuffle(playingQueue)
        playingQueue.clear()
        playingQueue.addAll(copy)

        val currentIdInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val songPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        if (songPosition != 0) {
            playingQueue.swap(0, songPosition)
        }

        updateCurrentSongPosition(playingQueue, 0, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    @MainThread
    fun sort() {
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
    fun onRepeatModeChanged() {
        assertMainThread()

        currentSongPosition = ensurePosition(playingQueue, currentSongPosition)
        var list =
            playingQueue.drop(currentSongPosition + 1).take(PlayingQueueGateway.MINI_QUEUE_SIZE)
                .toMutableList()
        list = handleQueueOnRepeatMode(list)

        try {
            queueMediaSession.onNext(list)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
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

    @MainThread
    fun handleSwap(from: Int, to: Int) {
        assertMainThread()

        if (from !in 0..playingQueue.lastIndex || to !in 0..playingQueue.lastIndex) {
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

        if (position !in 0..playingQueue.lastIndex) {
            return false
        }

        if (position >= 0 || position < playingQueue.size) {
            // todo case only one song

            playingQueue.removeAt(position)
            if (position <= currentSongPosition) {
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

    fun computePositionInQueue(
        list: List<MediaEntity>,
        position: Int
    ): dev.olog.service.music.model.PositionInQueue {
        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> dev.olog.service.music.model.PositionInQueue.IN_MIDDLE
            position == 0 && position == list.lastIndex -> dev.olog.service.music.model.PositionInQueue.FIRST_AND_LAST
            position == 0 -> dev.olog.service.music.model.PositionInQueue.FIRST
            position == list.lastIndex -> dev.olog.service.music.model.PositionInQueue.LAST
            else -> dev.olog.service.music.model.PositionInQueue.IN_MIDDLE
        }
    }

    fun currentPositionInQueue(): dev.olog.service.music.model.PositionInQueue {
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
                val track = if (isPodcast) {
                    val mediaId =
                        MediaId.createCategoryValue(MediaIdCategory.PODCASTS, it.toString())
                    podcastGateway.getByParam(mediaId.categoryId)
                        ?.toMediaEntity(maxProgressive++, mediaId)
                } else {
                    val mediaId =
                        MediaId.createCategoryValue(MediaIdCategory.PODCASTS, it.toString())
                    songGateway.getByParam(mediaId.categoryId)
                        ?.toMediaEntity(maxProgressive++, mediaId)
                }
                if (track == null) Maybe.empty<MediaEntity>() else Maybe.just(track)
            }
            .toList()
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
                val track = if (isPodcast) {
                    val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS, it.toString())
                    podcastGateway.getByParam(mediaId.categoryId)
                } else {
                    val mediaId = MediaId.createCategoryValue(MediaIdCategory.SONGS, it.toString())
                    songGateway.getByParam(mediaId.categoryId)
                }
                if (track == null) Maybe.empty<Song>() else Maybe.just(track)
            }
            .toList()
            .subscribe({ items ->
                var currentProgressive = before.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
                val listToAdd = items.map { item ->
                    item.toMediaEntity(currentProgressive++, item.getMediaId())
                }
                val afterListUpdated = after.map { it.copy(idInPlaylist = currentProgressive++) }

                val copy = before.asSequence().plus(listToAdd).plus(afterListUpdated).toList()
                updatePlayingQueueAndPersist(copy)
                onRepeatModeChanged() // not really but updates mini queue
            }, Throwable::printStackTrace)
    }


}