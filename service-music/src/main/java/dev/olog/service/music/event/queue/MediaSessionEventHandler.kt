package dev.olog.service.music.event.queue

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.schedulers.Schedulers
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.exhaustive
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ServiceScoped
internal class MediaSessionEventHandler @Inject constructor(
    private val schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val player: IPlayer,
    private val dataRetriever: DataRetriever,
) {

    private val events = ConflatedSharedFlow<MediaSessionEvent?>(null)
    private var lastPreparation: MediaSessionEvent.Prepare? = null

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

        lastPreparation = event

//        val track = queue.updateQueue(items)
//        player.prepare(track)
    }

    private suspend fun handlePlayFrom(event: MediaSessionEvent.Play) {
        if (!event.isQueueAlreadyPrepared(lastPreparation)) {
            handlePrepare(event.getPrepareQueueEvent())
        }
        lastPreparation = null

        player.resume()
    }

    private suspend fun handlePlayerActions(
        event: MediaSessionEvent.PlayerAction
    ) : Unit = withContext(NonCancellable) {
        if (!queue.isValidQueue) {
            handlePrepare(MediaSessionEvent.Prepare.LastQueue)
        }

        when (event) {
            is MediaSessionEvent.PlayerAction.Resume -> TODO()
            is MediaSessionEvent.PlayerAction.Pause -> TODO()
//            is MediaSessionEvent.PlayerAction.Stop -> TODO()
//            is MediaSessionEvent.PlayerAction.FastForward -> TODO()
//            is MediaSessionEvent.PlayerAction.Rewind -> TODO()
            is MediaSessionEvent.PlayerAction.SeekTo -> TODO()
//            is MediaSessionEvent.PlayerAction.PlaybackSpeed -> TODO()
            is MediaSessionEvent.PlayerAction.SkipToPrevious -> TODO()
            is MediaSessionEvent.PlayerAction.SkipToNext -> TODO()
//            is MediaSessionEvent.PlayerAction.SkipToQueueItem -> TODO()
            is MediaSessionEvent.PlayerAction.SkipToItem -> TODO()
            is MediaSessionEvent.PlayerAction.Forward10Seconds -> TODO()
            is MediaSessionEvent.PlayerAction.Forward30Seconds -> TODO()
            is MediaSessionEvent.PlayerAction.Replay10Seconds -> TODO()
            is MediaSessionEvent.PlayerAction.Replay30Seconds -> TODO()
        }.exhaustive
        // TODO play event
    }


}