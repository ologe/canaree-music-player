package dev.olog.service.music.event.queue

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.service.music.data.DataRetriever
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.queue.Queue
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.service.music.state.MusicServiceShuffleMode
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.android.BundleDictionary
import dev.olog.shared.exhaustive
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ServiceScoped
internal class MediaSessionEventHandler @Inject constructor(
    private val schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val player: IPlayer,
    private val dataRetriever: DataRetriever,
    private val queue: Queue,
    private val favoriteGateway: FavoriteGateway,
    private val repeatMode: MusicServiceRepeatMode,
    private val shuffleMode: MusicServiceShuffleMode,
) {

    private val events = ConflatedSharedFlow<MediaSessionEvent?>(null)
    private var lastPreparedEvent: MediaSessionEvent.Prepare? = null

    init {
        events.filterNotNull()
            .mapLatest(this::handleEvent)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    fun nextEvent(event: MediaSessionEvent) {
        events.tryEmit(event)
    }

    private suspend fun handleEvent(event: MediaSessionEvent) = when (event) {
        is MediaSessionEvent.Prepare -> handlePrepare(event)
        is MediaSessionEvent.Play -> handlePlayFrom(event)
        is MediaSessionEvent.PlayerAction -> handlePlayerActions(event)
        is MediaSessionEvent.ToggleFavorite -> handleToggleFavorite()
        is MediaSessionEvent.RepeatModeChanged -> handleToggleRepeatMode()
        is MediaSessionEvent.ShuffleModeChanged -> handleToggleShuffleMode()
        is MediaSessionEvent.CustomAction -> handleCustomAction(event.action, event.extras)
    }

    private suspend fun handlePrepare(event: MediaSessionEvent.Prepare) = withContext(schedulers.io) {
        val items = when (event) {
            is MediaSessionEvent.Prepare.LastQueue -> withContext(NonCancellable) {
                dataRetriever.getLastQueue()
            }
            is MediaSessionEvent.Prepare.FromMediaId -> dataRetriever.getFromMediaId(event.mediaId, event.extras)
            is MediaSessionEvent.Prepare.FromSearch -> dataRetriever.getFromSearch(event.query, event.extras)
            is MediaSessionEvent.Prepare.FromUri -> dataRetriever.getFromUri(event.uri, event.extras)
        }.exhaustive

        lastPreparedEvent = event

        // TODO handle empty
        val track = queue.updateQueue(event, items) ?: return@withContext

        // non cancellable is mandatory
        withContext(NonCancellable + schedulers.main) {
            player.prepare(
                playerModel = track,
                forcePause = event is MediaSessionEvent.Prepare.LastQueue
            )
        }
    }

    private suspend fun handlePlayFrom(event: MediaSessionEvent.Play) {
        val bookmark = withContext(schedulers.main) { player.getBookmark() }
        queue.updatePodcastPosition(bookmark)

        if (!event.isQueueAlreadyPrepared(lastPreparedEvent)) {
            handlePrepare(event.getPrepareQueueEvent())
        }
        lastPreparedEvent = null

        // TODO handle empty
        withContext(schedulers.main) {
            player.resume()
        }
    }

    private suspend fun handlePlayerActions(
        event: MediaSessionEvent.PlayerAction
    ) : Unit = withContext(NonCancellable + schedulers.main) {
        if (!queue.isValidQueue) { // TODO should not be needed
            handlePrepare(MediaSessionEvent.Prepare.LastQueue)
        }

        when (event) {
            is MediaSessionEvent.PlayerAction.Resume -> player.resume()
            is MediaSessionEvent.PlayerAction.Pause -> player.pause(
                stopService = event.stopService,
                releaseFocus = event.releaseFocus
            )
            is MediaSessionEvent.PlayerAction.SeekTo -> player.seekTo(event.millis)
            is MediaSessionEvent.PlayerAction.SkipToPrevious -> handleSkipPrevious()
            is MediaSessionEvent.PlayerAction.SkipToNext -> handleSkipNext(event.ended)
            is MediaSessionEvent.PlayerAction.SkipToItem -> TODO()
            is MediaSessionEvent.PlayerAction.Forward10Seconds -> player.forwardTenSeconds()
            is MediaSessionEvent.PlayerAction.Forward30Seconds -> player.forwardThirtySeconds()
            is MediaSessionEvent.PlayerAction.Replay10Seconds -> player.replayTenSeconds()
            is MediaSessionEvent.PlayerAction.Replay30Seconds -> player.replayThirtySeconds()
        }.exhaustive
    }

    private suspend fun handleSkipNext(trackEnded: Boolean) {
        val track = queue.getNextTrack(trackEnded) ?: return
        withContext(schedulers.main) {
            player.prepare(playerModel = track, forcePause = false)
            player.resume()
        }
    }

    private suspend fun handleSkipPrevious() {
        val bookmark = withContext(schedulers.main) { player.getBookmark() }
        val track = queue.getPreviousTrack(bookmark) ?: return
        withContext(schedulers.main) {
            player.prepare(playerModel = track, forcePause = false)
            player.resume()
        }
    }

    private suspend fun handleToggleFavorite() = withContext(NonCancellable) {
        favoriteGateway.toggleFavorite()
    }

    private fun handleToggleShuffleMode() {
        shuffleMode.toggle()
        // TODO update skip to actions
    }

    private fun handleToggleRepeatMode() {
        repeatMode.toggle()
        // TODO update skip to actions
    }

    private suspend fun handleCustomAction(
        action: MusicServiceCustomAction,
        extras: BundleDictionary?,
    ): Unit = withContext(NonCancellable) {
        when (action) {
            MusicServiceCustomAction.SHUFFLE -> {
                val shuffleEvent = MediaSessionEvent.Play.FromMediaId(
                    mediaId = MediaId.shuffleId(),
                    extras = BundleDictionary(emptyMap())
                )
                nextEvent(shuffleEvent)
            }
            MusicServiceCustomAction.SWAP -> {
                val from = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_SWAP_FROM) ?: 0
                val to = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_SWAP_TO) ?: 0
                queue.swap(from, to)
            }
            MusicServiceCustomAction.SWAP_RELATIVE -> {
                val from = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_SWAP_FROM) ?: 0
                val to = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_SWAP_TO) ?: 0
                queue.swapRelative(from, to)
            }
            MusicServiceCustomAction.REMOVE -> {
                val position = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_POSITION) ?: 0
                queue.remove(position)
            }
            MusicServiceCustomAction.REMOVE_RELATIVE -> {
                val position = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_POSITION) ?: 0
                queue.removeRelative(position)
            }
            MusicServiceCustomAction.MOVE_RELATIVE -> {
                val position = extras?.getTyped<Int>(MusicServiceCustomAction.ARGUMENT_POSITION) ?: 0
                queue.moveRelative(position)
            }
            MusicServiceCustomAction.FORWARD_10 -> player.forwardTenSeconds()
            MusicServiceCustomAction.FORWARD_30 -> player.forwardThirtySeconds()
            MusicServiceCustomAction.REPLAY_10 -> player.replayTenSeconds()
            MusicServiceCustomAction.REPLAY_30 -> player.replayThirtySeconds()
            MusicServiceCustomAction.ADD_TO_PLAY_LATER -> {
                val ids = extras?.getTyped<LongArray>(MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST)?.toList()
                val isPodcast = extras?.getTyped<Boolean>(MusicServiceCustomAction.ARGUMENT_IS_PODCAST) ?: false
                queue.addToPlayLater(ids.orEmpty(), isPodcast)
            }
            MusicServiceCustomAction.ADD_TO_PLAY_NEXT -> {
                val ids = extras?.getTyped<LongArray>(MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST)?.toList()
                val isPodcast = extras?.getTyped<Boolean>(MusicServiceCustomAction.ARGUMENT_IS_PODCAST) ?: false
                queue.addToPlayNext(ids.orEmpty(), isPodcast)
            }
            MusicServiceCustomAction.TOGGLE_FAVORITE -> error("invalid action=$action")
        }
    }


}