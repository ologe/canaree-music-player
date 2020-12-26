package dev.olog.service.music.queue

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaIdModifier
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.PodcastPositionUseCase
import dev.olog.core.interactor.UpdatePlayingQueueUseCase
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.lib.media.model.PlayerRepeatMode
import dev.olog.lib.media.model.PlayerShuffleMode
import dev.olog.service.music.event.queue.MediaSessionEvent
import dev.olog.service.music.model.*
import dev.olog.service.music.state.MusicServicePlaybackState
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.service.music.state.MusicServiceShuffleMode
import dev.olog.shared.indexOfFirstOrNull
import dev.olog.shared.swapped
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ServiceScoped
internal class Queue @Inject constructor(
    lifecycleOwner: LifecycleOwner,
    schedulers: Schedulers,
    private val updatePlayingQueueUseCase: UpdatePlayingQueueUseCase,
    private val queueMediaSession: MediaSessionQueue,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val repeatMode: MusicServiceRepeatMode,
    private val shuffleMode: MusicServiceShuffleMode,
    private val podcastPosition: PodcastPositionUseCase,
    private val enhancedShuffle: EnhancedShuffle,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val playbackState: MusicServicePlaybackState,
) {

    companion object {
        const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec
    }

    private val _queueState: MutableStateFlow<QueueState> = MutableStateFlow(QueueState.NotSet)
    private val queueState: QueueState
        get() = _queueState.value

    val isValidQueue: Boolean
        get() = queueState is QueueState.Set

    init {
        val queueFlow = _queueState
            .filterIsInstance<QueueState.Set>()

        queueFlow.map { it.queue }
            .distinctUntilChanged()
            .mapLatest(this::persistQueue)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)

        queueFlow
            .mapLatest(this::publishMiniQueue)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)

        queueFlow.map { it.position }
            .distinctUntilChanged()
            .mapLatest { musicPreferencesUseCase.lastProgressive = it }
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)

        queueFlow.filterIsInstance<QueueState.Set>()
            .mapLatest { playbackState.toggleSkipToActions(computePositionInQueue()) }
            .launchIn(lifecycleOwner.lifecycleScope)

        repeatMode.state
            .mapLatest(this::onRepeatModeChanged)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)

        shuffleMode.state
            .mapLatest(this::onShuffleModeChanged)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun updateQueue(items: List<MediaEntity>, position: Int): QueueState {
        val newState = if (items.isEmpty()) {
            QueueState.Empty
        } else {
            QueueState.Set(
                position = if (position in 0..items.lastIndex) position else 0,
                queue = items
            )
        }
        _queueState.value = newState

        return newState
    }

    suspend fun updateQueue(
        event: MediaSessionEvent.Prepare,
        items: List<MediaEntity>
    ): PlayerMediaEntity? {
        val mediaIdEvent = event as? MediaSessionEvent.Prepare.FromMediaId
        val shuffleMode = if (mediaIdEvent?.mediaId?.modifier == MediaIdModifier.SHUFFLE) {
            PlayerShuffleMode.ENABLED
        } else {
            // always disable shuffle, except on explicit SHUFFLE
            PlayerShuffleMode.DISABLED
        }
        this.shuffleMode.setEnabled(shuffleMode)

        val current = when (event) {
            is MediaSessionEvent.Prepare.LastQueue -> musicPreferencesUseCase.lastProgressive
            is MediaSessionEvent.Prepare.FromMediaId -> {
                val songId = event.mediaId.leaf
                items.getCurrentSongIndexWhenPlayingNewQueue(songId)
            }
            is MediaSessionEvent.Prepare.FromSearch -> 0
            is MediaSessionEvent.Prepare.FromUri -> 0
        }

        updateQueue(items, current)

        return current(event)
    }

    private suspend fun current(
        event: MediaSessionEvent.Prepare
    ): PlayerMediaEntity? = queueState.whenIsSet {

        val bookmark = when (event) {
            is MediaSessionEvent.Prepare.LastQueue -> getLastSessionBookmark(entity)
            is MediaSessionEvent.Prepare.FromMediaId -> getPodcastBookmarkOrZero(entity)
            is MediaSessionEvent.Prepare.FromSearch -> getPodcastBookmarkOrZero(entity)
            is MediaSessionEvent.Prepare.FromUri -> getPodcastBookmarkOrZero(entity)
        }

        return@whenIsSet entity.toPlayerMediaEntity(
            positionInQueue = computePositionInQueue(),
            bookmark = bookmark,
            skipType = SkipType.NONE,
        )
    }

    private suspend fun getLastSessionBookmark(mediaEntity: MediaEntity): Long  {
        if (mediaEntity.isPodcast) {
            val bookmark = podcastPosition.get(mediaEntity.id, mediaEntity.duration)
            return bookmark.coerceIn(0L, mediaEntity.duration)
        } else {
            val bookmark = musicPreferencesUseCase.getBookmark()
            return bookmark.coerceIn(0L, mediaEntity.duration)
        }
    }

    private suspend fun getPodcastBookmarkOrZero(
        mediaEntity: MediaEntity
    ): Long {
        return if (mediaEntity.isPodcast) {
            val bookmark = podcastPosition.get(mediaEntity.id, mediaEntity.duration)
            bookmark.coerceIn(0L, mediaEntity.duration)
        } else {
            0L
        }
    }

    private suspend fun persistQueue(items: List<MediaEntity>) {
        val request = items.map {
            UpdatePlayingQueueUseCaseRequest(
                mediaId = it.mediaId,
                songId = it.id,
                idInPlaylist = it.progressive
            )
        }
        updatePlayingQueueUseCase(request)
    }

    private fun publishMiniQueue(queueState: QueueState.Set) {
        val miniQueue = queueState.queue.asSequence()
            .drop(queueState.position + 1)
            .take(PlayingQueueGateway.MINI_QUEUE_SIZE)
            .toMutableList()
            .handleQueueOnRepeatMode(queueState.queue)

        queueMediaSession.onNext(miniQueue)
    }

    @CheckResult
    private fun MutableList<MediaEntity>.handleQueueOnRepeatMode(
        original: List<MediaEntity>
    ): MutableList<MediaEntity> {
        val copy = this.toMutableList()

        if (copy.size < PlayingQueueGateway.MINI_QUEUE_SIZE && repeatMode.isRepeatAll()) {
            // repeat all, show another copy of the list ah the end
            copy.addAll(original.take(PlayingQueueGateway.MINI_QUEUE_SIZE))
            return copy.asSequence().take(PlayingQueueGateway.MINI_QUEUE_SIZE).toMutableList()
        }
        return copy
    }

    private fun computePositionInQueue(): PositionInQueue {
        val state = queueState as? QueueState.Set ?: return PositionInQueue.FIRST
        val items = state.queue
        val position = state.position

        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            position == 0 && position == items.lastIndex -> PositionInQueue.FIRST_AND_LAST
            position == 0 -> PositionInQueue.FIRST
            position == items.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }

    private fun List<MediaEntity>.getCurrentSongIndexWhenPlayingNewQueue(songId: Long?): Int {
        if (shuffleMode.isEnabled() || songId == null) {
            return 0
        } else {
            return this.indexOfFirst { it.id == songId }.coerceIn(0, this.lastIndex)
        }
    }

    private fun onRepeatModeChanged(unused: PlayerRepeatMode) {
        val state = this.queueState
        if (state is QueueState.Set) {
            publishMiniQueue(state)
        }
    }

    private suspend fun onShuffleModeChanged(state: PlayerShuffleMode) = when (state) {
        PlayerShuffleMode.DISABLED -> sort()
        PlayerShuffleMode.ENABLED -> shuffle()
    }

    private suspend fun sort() = queueState.whenIsSet {

        val newQueue = queue.sortedBy { it.progressive }
        val newPosition = newQueue.indexOfFirst { it.progressive == entity.progressive }

        updateQueue(newQueue, newPosition)
    }

    private suspend fun shuffle() = queueState.whenIsSet {

        val newQueue = enhancedShuffle(queue)
        val newPosition = newQueue.indexOfFirst { it.progressive == entity.progressive }

        updateQueue(newQueue, newPosition)
    }

    suspend fun getNextTrack(
        trackEnded: Boolean
    ): PlayerMediaEntity? = queueState.whenIsSet {
        val skipType = if (trackEnded) SkipType.TRACK_ENDED else SkipType.SKIP_NEXT

        if (repeatMode.isRepeatOne() && trackEnded) {
            // restart
            return@whenIsSet entity.toPlayerMediaEntity(
                positionInQueue = computePositionInQueue(),
                bookmark = getPodcastBookmarkOrZero(entity),
                skipType = skipType,
            )
        }
        var newPosition = queue.indexOf(entity) + 1
        if (newPosition > queue.lastIndex && repeatMode.isRepeatAll()) {
            newPosition = 0
        }
        if (newPosition in 0..queue.lastIndex) {
            val newState = updateQueue(queue, newPosition) as? QueueState.Set
            val newEntity = newState?.entity
            return@whenIsSet newEntity?.toPlayerMediaEntity(
                positionInQueue = computePositionInQueue(),
                bookmark = getPodcastBookmarkOrZero(newEntity),
                skipType = skipType,
            )
        }
        return@whenIsSet null
    }

    suspend fun getPreviousTrack(
        bookmark: Long
    ): PlayerMediaEntity? = queueState.whenIsSet {
        val isPodcast = entity.isPodcast

        if (!isPodcast && bookmark > SKIP_TO_PREVIOUS_THRESHOLD) {
            // restart
            return@whenIsSet entity.toPlayerMediaEntity(
                positionInQueue = computePositionInQueue(),
                bookmark = bookmark,
                skipType = SkipType.RESTART,
            )
        }

        val currentPosition = queue.indexOf(entity)
        var newPosition = currentPosition - 1
        if (currentPosition == 0 && newPosition < 0 && !repeatMode.isRepeatAll()) {
            // restart song from beginning if is first
            return@whenIsSet entity.toPlayerMediaEntity(
                positionInQueue = computePositionInQueue(),
                bookmark = bookmark,
                skipType = SkipType.RESTART,
            )
        }

        if (newPosition < 0 && repeatMode.isRepeatAll()) {
            newPosition = queue.lastIndex
        }

        if (newPosition in 0..queue.lastIndex) {
            val newState = updateQueue(queue, newPosition) as? QueueState.Set
            val newEntity = newState?.entity
            return@whenIsSet newEntity?.toPlayerMediaEntity(
                positionInQueue = computePositionInQueue(),
                bookmark = bookmark,
                skipType = SkipType.SKIP_PREVIOUS,
            )
        }
        return@whenIsSet null
    }

    suspend fun swapRelative(from: Int, to: Int) = queueState.whenIsSet {
        swap(from + position + 1, to + position + 1)
    }

    suspend fun swap(
        from: Int,
        to: Int
    ) = queueState.whenIsSet {
        if (from !in 0..queue.lastIndex || to !in 0..queue.lastIndex) {
            return@whenIsSet
        }
        val newQueue = queue.swapped(from, to)
        val newPosition = newQueue.indexOfFirst { it.progressive == entity.progressive }
        updateQueue(newQueue, newPosition)
    }

    suspend fun removeRelative(pos: Int) = queueState.whenIsSet {
        remove(pos + position + 1)
    }

    suspend fun remove(pos: Int) = queueState.whenIsSet {
        if (pos !in 0..queue.lastIndex) {
            return@whenIsSet
        }
        val newQueue = queue.toMutableList().apply { removeAt(pos) }
        val newPosition = newQueue.indexOfFirstOrNull { it.progressive == entity.progressive } ?: 0
        updateQueue(newQueue, newPosition)
    }

    suspend fun moveRelative(pos: Int) = queueState.whenIsSet {
        if (pos !in 0..queue.lastIndex) {
            return@whenIsSet
        }

        val relativePosition = pos + position + 1
        val newQueue = queue.toMutableList()
        val item = newQueue.removeAt(relativePosition)
        newQueue.add(position + 1, item)
        val newIndex = newQueue.indexOfFirst { it.progressive == entity.progressive }

        updateQueue(newQueue, newIndex)
    }

    suspend fun addToPlayLater(
        ids: List<Long>,
        isPodcast: Boolean,
    ) = queueState.whenIsSet {
        var maxProgressive = queue.maxByOrNull { it.progressive }?.progressive ?: 0

        val tracksToAdd = ids.mapNotNull { findTrack(it, isPodcast) }
            .map { it.toMediaEntity(++maxProgressive, it.getMediaId()) }

        val newQueue = queue + tracksToAdd

        updateQueue(newQueue, position)
    }

    suspend fun addToPlayNext(
        ids: List<Long>,
        isPodcast: Boolean,
    ) = queueState.whenIsSet {
        val before = queue.take(position + 1)
        val after = queue.drop(position + 1)

        var maxProgressive = queue.maxByOrNull { it.progressive }?.progressive ?: 0

        val tracksToAdd = ids.mapNotNull { findTrack(it, isPodcast) }
            .map { it.toMediaEntity(++maxProgressive, it.getMediaId()) }

        val newQueue = before + tracksToAdd + after
        val newPosition = newQueue.indexOfFirstOrNull { it.progressive == entity.progressive } ?: 0

        updateQueue(newQueue, newPosition)
    }

    private suspend fun findTrack(id: Long, isPodcast: Boolean): Song? {
        return if (isPodcast) {
            podcastGateway.getByParam(id)
        } else {
            songGateway.getByParam(id)
        }
    }

    suspend fun updatePodcastPosition(bookmark: Long) = queueState.whenIsSet {
        if (entity.isPodcast) {
            podcastPosition.set(entity.id, bookmark)
        }
    }

}