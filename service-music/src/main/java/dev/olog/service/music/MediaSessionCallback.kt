package dev.olog.service.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.coroutines.DispatcherScope
import dev.olog.core.coroutines.autoDisposeJob
import dev.olog.domain.MediaId
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IQueue
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.queue.SKIP_TO_PREVIOUS_THRESHOLD
import dev.olog.service.music.state.MusicServicePlaybackState
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.service.music.state.MusicServiceShuffleMode
import dev.olog.shared.android.utils.assertBackgroundThread
import dev.olog.shared.android.utils.assertMainThread
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@PerService
internal class MediaSessionCallback @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val queue: IQueue,
    private val player: IPlayer,
    private val repeatMode: MusicServiceRepeatMode,
    private val shuffleMode: MusicServiceShuffleMode,
    private val mediaButton: MediaButton,
    private val playerState: MusicServicePlaybackState,
    private val favoriteGateway: FavoriteGateway,
    private val schedulers: Schedulers

) : MediaSessionCompat.Callback(),
    DefaultLifecycleObserver {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionCallback::class.java.simpleName}"
    }

    private val scope by DispatcherScope(schedulers.cpu)
    private var retrieveDataJob by autoDisposeJob()

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        scope.cancel()
    }

    override fun onPrepare() {
        onPrepareInternal(forced = true)
    }

    private fun onPrepareInternal(forced: Boolean){
        assertMainThread()

        if (queue.isEmpty() || forced){
            val track = queue.prepare()
            Timber.v("$TAG onPrepare with track=${track?.mediaEntity?.title}")
            if (track != null){
                player.prepare(track)
            }
        }
    }

    private fun retrieveAndPlay(retrieve: suspend () -> PlayerMediaEntity?) {
        retrieveDataJob = scope.launch {
            assertBackgroundThread()
            val entity = retrieve()
            if (entity != null) {
                withContext(schedulers.main) {
                    player.play(entity)
                }
            } else {
                onEmptyQueue()
            }
        }
    }

    private fun onEmptyQueue() {
        Timber.v("$TAG onEmptyQueue")
        onStop()
    }

    override fun onPlayFromMediaId(stringMediaId: String, extras: Bundle?) {
        Timber.v("$TAG onPlayFromMediaId mediaId=$stringMediaId, extras=$extras")

        onPrepareInternal(false)

        retrieveAndPlay {
            updatePodcastPosition()

            when (val mediaId = MediaId.fromString(stringMediaId)) {
                MediaId.SHUFFLE_ID -> {
                    // android auto call 'onPlayFromMediaId' with 'MediaId.shuffleId()'
                    queue.handlePlayShuffle(MediaId.SHUFFLE_ID, null)
                }
                else -> {
                    val filter = extras!!.getString(MusicServiceCustomAction.ARGUMENT_FILTER)
                    queue.handlePlayFromMediaId(mediaId, filter)
                }
            }
        }
    }

    override fun onPlay() {
        onPrepareInternal(false)
        Timber.v("$TAG onPlay")
        player.resume()
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        onPrepareInternal(false)
        Timber.v("$TAG onPlayFromSearch query=$query, extras=$extras")

        retrieveAndPlay {
            updatePodcastPosition()
            queue.handlePlayFromGoogleSearch(query, extras)
        }
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        onPrepareInternal(false)
        Timber.v("$TAG onPlayFromUri uri=$uri, extras=$extras")

        retrieveAndPlay {
            updatePodcastPosition()
            queue.handlePlayFromUri(uri)
        }
    }

    override fun onPause() {
        Timber.v("$TAG onPause")
        scope.launch(schedulers.main) {
            updatePodcastPosition()
            player.pause(true)
        }
    }

    override fun onStop() {
        Timber.v("$TAG onStop")
        onPause()
    }

    override fun onSkipToNext() {
        Timber.v("$TAG onSkipToNext")
        onSkipToNext(false)
    }

    override fun onSkipToPrevious() {
        scope.launch(schedulers.main) {
            Timber.v("$TAG onSkipToPrevious")

            updatePodcastPosition()
            queue.handleSkipToPrevious(player.getBookmark())?.let { metadata ->

                val skipType = if (!metadata.mediaEntity.isPodcast && player.getBookmark() > SKIP_TO_PREVIOUS_THRESHOLD)
                        SkipType.RESTART else SkipType.SKIP_PREVIOUS
                player.playNext(metadata, skipType)
            }
        }
    }

    private fun onTrackEnded() {
        Timber.v("$TAG onTrackEnded")
        onSkipToNext(true)
    }

    /**
     * Try to skip to next song, if can't, restart current and pause
     */
    private fun onSkipToNext(trackEnded: Boolean) = scope.launch(schedulers.main) {
        Timber.v("$TAG onSkipToNext internal track ended=$trackEnded")
        updatePodcastPosition()
        val metadata = queue.handleSkipToNext(trackEnded)
        if (metadata != null) {
            val skipType = if (trackEnded) SkipType.TRACK_ENDED else SkipType.SKIP_NEXT
            player.playNext(metadata, skipType)
        } else {
            val currentSong = queue.getPlayingSong()
            if (currentSong != null) {
                player.play(currentSong)
                player.pause(true)
                player.seekTo(0L)
            } else {
                onEmptyQueue()
            }
        }
    }

    override fun onSkipToQueueItem(id: Long) {
        scope.launch(schedulers.main) {
            Timber.v("$TAG onSkipToQueueItem id=$id")

            updatePodcastPosition()
            val mediaEntity = queue.handleSkipToQueueItem(id)
            if (mediaEntity != null) {
                player.play(mediaEntity)
            } else {
                onEmptyQueue()
            }
        }
    }

    override fun onSeekTo(pos: Long) {
        Timber.v("$TAG onSeekTo pos=$pos")
        scope.launch(schedulers.main) {
            updatePodcastPosition()
            player.seekTo(pos)
        }
    }

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        Timber.v("$TAG onSetRating rating=$rating, extras=$extras")
        scope.launch { favoriteGateway.toggleFavorite() }
    }

    override fun onCustomAction(action: String, extras: Bundle?) {
        onPrepareInternal(false)
        Timber.v("$TAG onCustomAction action=$action, extras=$extras")

        val musicAction = MusicServiceCustomAction.values().find { it.name == action }
            ?: return // other apps can request custom action

        val exhaustive: Any = when (musicAction) {
            MusicServiceCustomAction.SHUFFLE -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                val filter = extras.getString(MusicServiceCustomAction.ARGUMENT_FILTER)
                retrieveAndPlay {
                    updatePodcastPosition()
                    queue.handlePlayShuffle(MediaId.fromString(mediaId) as MediaId.Category, filter)
                }
            }
            MusicServiceCustomAction.SWAP -> {
                requireNotNull(extras)
                val from = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_TO, 0)
                queue.handleSwap(from, to)
            }
            MusicServiceCustomAction.SWAP_RELATIVE -> {
                requireNotNull(extras)
                val from = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_TO, 0)
                queue.handleSwapRelative(from, to)
            }
            MusicServiceCustomAction.REMOVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION, -1)
                queue.handleRemove(position)
            }
            MusicServiceCustomAction.REMOVE_RELATIVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION, -1)
                queue.handleRemoveRelative(position)
            }
            MusicServiceCustomAction.PLAY_RECENTLY_ADDED -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                retrieveAndPlay {
                    updatePodcastPosition()
                    queue.handlePlayRecentlyAdded(MediaId.fromString(mediaId) as MediaId.Track)
                }
            }
            MusicServiceCustomAction.PLAY_MOST_PLAYED -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                retrieveAndPlay {
                    updatePodcastPosition()
                    queue.handlePlayMostPlayed(MediaId.fromString(mediaId) as MediaId.Track)
                }
            }
            MusicServiceCustomAction.PLAY_SPOTIFY_PREVIEW -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                retrieveAndPlay {
                    updatePodcastPosition()
                    queue.handlePlaySpotifyPreview(MediaId.fromString(mediaId) as MediaId.Track)
                }
            }
            MusicServiceCustomAction.FORWARD_10 -> player.forwardTenSeconds()
            MusicServiceCustomAction.FORWARD_30 -> player.forwardThirtySeconds()
            MusicServiceCustomAction.REPLAY_10 -> player.replayTenSeconds()
            MusicServiceCustomAction.REPLAY_30 -> player.replayThirtySeconds()
            MusicServiceCustomAction.TOGGLE_FAVORITE -> onSetRating(null)
            MusicServiceCustomAction.ADD_TO_PLAY_LATER -> {
                scope.launch {
                    requireNotNull(extras)
                    val mediaIds =
                        extras.getLongArray(MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST)!!

                    val position = queue.playLater(mediaIds.toList())
                    playerState.toggleSkipToActions(position)
                }
            }
            MusicServiceCustomAction.ADD_TO_PLAY_NEXT -> {
                scope.launch {
                    requireNotNull(extras)
                    val mediaIds =
                        extras.getLongArray(MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST)!!

                    val position = queue.playNext(mediaIds.toList())
                    playerState.toggleSkipToActions(position)
                }
            }
            MusicServiceCustomAction.MOVE_RELATIVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION)
                queue.handleMoveRelative(position)
            }
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        Timber.v("$TAG onSetRepeatMode")

        this.repeatMode.update()
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
        queue.onRepeatModeChanged()
    }

    override fun onSetShuffleMode(unused: Int) {
        Timber.v("$TAG onSetShuffleMode")

        val newShuffleMode = this.shuffleMode.update()
        if (newShuffleMode) {
            queue.shuffle()
        } else {
            queue.sort()
        }
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
    }

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        onPrepareInternal(false)

        val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)!!
        Timber.v("$TAG onMediaButtonEvent, action=${event.action}, keycode=${event.keyCode}")
        if (event.action == KeyEvent.ACTION_DOWN) {

            when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> player.stopService()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> player.pause(false)
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                KeyEvent.KEYCODE_HEADSETHOOK -> mediaButton.onHeatSetHookClick()
                else -> throw IllegalArgumentException("not handled")
            }
        }

        return true
    }

    /**
     * this function DO NOT KILL service on pause
     */
    fun handlePlayPause() {
        Timber.v("$TAG handlePlayPause")

        if (player.isPlaying()) {
            player.pause(false)
        } else {
            onPlay()
        }
    }

    private suspend fun updatePodcastPosition() {
        Timber.v("$TAG updatePodcastPosition")

        val bookmark = withContext(schedulers.main) { player.getBookmark() }
        withContext(schedulers.io){
            queue.updatePodcastPosition(bookmark)
        }
    }

}