package dev.olog.service.music.internal

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IQueue
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.queue.SKIP_TO_PREVIOUS_THRESHOLD
import dev.olog.service.music.state.MusicServicePlaybackState
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.service.music.state.MusicServiceShuffleMode
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ServiceScoped
internal class MediaSessionEventDispatcher @Inject constructor(
    schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val queueManager: IQueue,
    private val player: IPlayer,
    private val favoriteGateway: FavoriteGateway,
    private val playerState: MusicServicePlaybackState,
    private val repeatMode: MusicServiceRepeatMode,
    private val shuffleMode: MusicServiceShuffleMode,
) {

    private val _current = MutableStateFlow<MediaEntity?>(null)
    private val events = MutableStateFlow<MediaSessionEvent?>(null)

    val current: Flow<MediaEntity>
        get() = _current.filterNotNull()

    init {
        events.filterNotNull()
            .mapLatest(this::handleEvent)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    fun nextEvent(event: MediaSessionEvent) {
        events.value = event
    }

    private suspend fun handleEvent(event: MediaSessionEvent) = when (event) {
        is MediaSessionEvent.Prepare -> prepare()
        is MediaSessionEvent.Resume -> resume()
        is MediaSessionEvent.Pause -> pause(event.stopService)
        is MediaSessionEvent.SkipToPrevious -> skipToPrevious()
        is MediaSessionEvent.SkipToNext -> skipToNext(event.ended)
        is MediaSessionEvent.SkipToItem -> skipToItem(event.id)
        is MediaSessionEvent.SeekTo -> seekTo(event.millis)
        is MediaSessionEvent.ToggleFavorite -> toggleFavorite()
        is MediaSessionEvent.Forward10Seconds -> forward10Seconds()
        is MediaSessionEvent.Forward30Seconds -> forward30Seconds()
        is MediaSessionEvent.Replay10Seconds -> replay10Seconds()
        is MediaSessionEvent.Replay30Seconds -> replay30Seconds()
        is MediaSessionEvent.RepeatModeChanged -> repeatModeChanged()
        is MediaSessionEvent.ShuffleModeChanged -> shuffleModeChanged()
        // play
        is MediaSessionEvent.PlayFromMediaId -> playFromMediaId(event.mediaId, event.filter)
        is MediaSessionEvent.PlayFromSearch -> playFromSearch(event.query, event.extras)
        is MediaSessionEvent.PlayFromUri -> playFromUri(event.uri)
        is MediaSessionEvent.PlayShuffle -> playShuffle(event.mediaId, event.filter)
        is MediaSessionEvent.PlayRecentlyAdded -> playRecentlyAdded(event.mediaId)
        is MediaSessionEvent.PlayMostPlayed -> playMostPlayed(event.mediaId)
        // edit
        is MediaSessionEvent.Swap -> swap(event.from, event.to)
        is MediaSessionEvent.SwapRelative -> swapRelative(event.from, event.to)
        is MediaSessionEvent.Remove -> remove(event.position)
        is MediaSessionEvent.RemoveRelative -> removeRelative(event.position)
        is MediaSessionEvent.AddToPlayLater -> addToPlayLater(event.ids, event.isPodcast)
        is MediaSessionEvent.AddToPlayNext -> addToPlayNext(event.ids, event.isPodcast)
        is MediaSessionEvent.MoveRelative -> moveRelative(event.position)
    }

    private suspend fun prepare() = withContext(NonCancellable) {
        val track = queueManager.prepare()
        if (track != null) {
            player.prepare(track)
        }
    }

    private suspend fun playFromMediaId(mediaId: MediaId, filter: String?) {
        updatePodcastPosition()

        val track = when (mediaId) {
            MediaId.shuffleId() -> {
                // android auto call 'onPlayFromMediaId' with 'MediaId.shuffleId()'
                queueManager.handlePlayShuffle(mediaId, null)
            }
            else -> queueManager.handlePlayFromMediaId(mediaId, filter)
        }

        postTrack(track)
    }

    private suspend fun playFromSearch(query: String, extras: Bundle) {
        updatePodcastPosition()

        val track = queueManager.handlePlayFromGoogleSearch(query, extras)
        postTrack(track)
    }

    private suspend fun playFromUri(uri: Uri) {
        updatePodcastPosition()

        val track = queueManager.handlePlayFromUri(uri)
        postTrack(track)
    }

    private suspend fun playShuffle(mediaId: MediaId, filter: String?) {
        updatePodcastPosition()

        val track = queueManager.handlePlayShuffle(mediaId, filter)
        postTrack(track)
    }

    private suspend fun playRecentlyAdded(mediaId: MediaId) {
        updatePodcastPosition()
        val track = queueManager.handlePlayRecentlyAdded(mediaId)
        postTrack(track)
    }

    private suspend fun playMostPlayed(mediaId: MediaId) {
        updatePodcastPosition()
        val track = queueManager.handlePlayMostPlayed(mediaId)
        postTrack(track)
    }

    private suspend fun resume() {
        player.resume()
    }

    private suspend fun pause(stopService: Boolean) {
        updatePodcastPosition()
        player.pause(stopService)
    }

    private suspend fun skipToPrevious() {
        updatePodcastPosition()

        val track = queueManager.handleSkipToPrevious(player.getBookmark())
        postPreviousTrack(track)
    }

    private suspend fun skipToNext(ended: Boolean) {
        updatePodcastPosition()

        val track = queueManager.handleSkipToNext(ended)
        postNextTrack(track, ended)
    }

    private suspend fun skipToItem(id: Long) {
        updatePodcastPosition()

        val track = queueManager.handleSkipToQueueItem(id)
        postTrack(track)
    }

    private suspend fun seekTo(millis: Long) {
        updatePodcastPosition()
        player.seekTo(millis)
    }

    private suspend fun forward10Seconds() {
        player.forwardTenSeconds()
    }

    private suspend fun forward30Seconds() {
        player.forwardThirtySeconds()
    }

    private suspend fun replay10Seconds() {
        player.replayTenSeconds()
    }

    private suspend fun replay30Seconds() {
        player.replayThirtySeconds()
    }

    private suspend fun toggleFavorite() = withContext(NonCancellable) {
        favoriteGateway.toggleFavorite()
    }

    private suspend fun swap(from: Int, to: Int) = withContext(NonCancellable) {
        queueManager.handleSwap(from, to)
    }

    private suspend fun swapRelative(from: Int, to: Int) = withContext(NonCancellable) {
        queueManager.handleSwapRelative(from, to)
    }

    private suspend fun remove(position: Int) = withContext(NonCancellable) {
        queueManager.handleRemove(position)
    }

    private suspend fun removeRelative(position: Int) = withContext(NonCancellable) {
        queueManager.handleRemoveRelative(position)
    }

    private suspend fun addToPlayLater(
        ids: List<Long>,
        isPodcast: Boolean,
    ) = withContext(NonCancellable) {
        val position = queueManager.playLater(ids, isPodcast)
        playerState.toggleSkipToActions(position)
    }

    private suspend fun addToPlayNext(
        ids: List<Long>,
        isPodcast: Boolean,
    ) = withContext(NonCancellable) {
        val position = queueManager.playNext(ids, isPodcast)
        playerState.toggleSkipToActions(position)
    }

    private suspend fun moveRelative(position: Int) = withContext(NonCancellable) {
        queueManager.handleMoveRelative(position)
    }

    private suspend fun repeatModeChanged() = withContext(NonCancellable) {
        repeatMode.update()
        playerState.toggleSkipToActions(queueManager.getCurrentPositionInQueue())
        queueManager.onRepeatModeChanged()
    }

    private suspend fun shuffleModeChanged() = withContext(NonCancellable) {
        val newShuffleMode = shuffleMode.update()
        if (newShuffleMode) {
            queueManager.shuffle()
        } else {
            queueManager.sort()
        }
        playerState.toggleSkipToActions(queueManager.getCurrentPositionInQueue())
    }

    private suspend fun updatePodcastPosition() {
        val bookmark = player.getBookmark()
        queueManager.updatePodcastPosition(bookmark)
    }

    private suspend fun postTrack(entity: PlayerMediaEntity?) {
        if (entity == null) {
            // TODO post empty
            return
        }
        player.play(entity)
        _current.value = entity.mediaEntity
    }

    private suspend fun postPreviousTrack(entity: PlayerMediaEntity?) {
        if (entity == null) {
            // TODO post empty ??
            return
        }
        val isPodcast = entity.mediaEntity.isPodcast

        val skipType = if (!isPodcast && player.getBookmark() > SKIP_TO_PREVIOUS_THRESHOLD) {
            SkipType.RESTART
        } else {
            SkipType.SKIP_PREVIOUS
        }
        player.playNext(entity, skipType)
        _current.value = entity.mediaEntity
    }

    private suspend fun postNextTrack(entity: PlayerMediaEntity?, ended: Boolean) {
        if (entity != null) {
            val skipType = if (ended) SkipType.TRACK_ENDED else SkipType.SKIP_NEXT
            player.playNext(entity, skipType)
            _current.value = entity.mediaEntity
            return
        }
        // restart current and pause
        val current = queueManager.getPlayingSong() ?: return
        player.play(current)
        player.pause(true)
        player.seekTo(0L)
    }

}