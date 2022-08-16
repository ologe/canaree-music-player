package dev.olog.feature.main

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import dev.olog.core.MediaId
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.api.MediaExposer
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.media.api.MusicServiceCustomAction
import dev.olog.feature.media.api.connection.OnConnectionChanged
import dev.olog.feature.media.api.extensions.playPause
import dev.olog.feature.media.api.model.PlayerItem
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.feature.media.api.model.PlayerRepeatMode
import dev.olog.platform.permission.PermissionManager
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.activity.ThemedActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class MusicGlueActivity : ThemedActivity(),
    MediaProvider,
    OnConnectionChanged {

    @Inject
    lateinit var featureMediaNavigator: FeatureMediaNavigator
    @Inject
    lateinit var permissionManager: PermissionManager

    private var connectionJob by autoDisposeJob()

    private val mediaExposer by lazyFast {
        MediaExposer(
            context = this,
            onConnectionChanged = this,
            scope = lifecycleScope,
            componentName = featureMediaNavigator.serviceComponent(),
            permissionManager = permissionManager,
        )
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        connectionJob = lifecycleScope.launch {
            mediaExposer.connect()
        }
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        connectionJob = null
        mediaExposer.disconnect()
        unregisterMediaController()
    }

    override fun onConnectedSuccess(
        mediaBrowser: MediaBrowserCompat,
        callback: MediaControllerCompat.Callback
    ) {
        try {
            registerMediaController(mediaBrowser.sessionToken, callback)
            mediaExposer.initialize(MediaControllerCompat.getMediaController(this))
        } catch (ex: Throwable) {
            ex.printStackTrace()
            onConnectedFailed(mediaBrowser, callback)
        }
    }

    override fun onConnectedFailed(
        mediaBrowser: MediaBrowserCompat,
        callback: MediaControllerCompat.Callback
    ) {
        unregisterMediaController()
    }

    private fun registerMediaController(
        token: MediaSessionCompat.Token,
        callback: MediaControllerCompat.Callback
    ) {
        val mediaController = MediaControllerCompat(this, token)
        mediaController.registerCallback(callback)
        MediaControllerCompat.setMediaController(this, mediaController)
    }

    private fun unregisterMediaController() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        if (mediaController != null) {
            mediaController.unregisterCallback(mediaExposer.callback)
            MediaControllerCompat.setMediaController(this, null)
        }
    }

    override fun observeMetadata(): Flow<PlayerMetadata> = mediaExposer.observeMetadata()

    override fun observePlaybackState(): Flow<PlayerPlaybackState> = mediaExposer.observePlaybackState()

    override fun observeRepeat(): Flow<PlayerRepeatMode> = mediaExposer.observeRepeat()

    override fun observeShuffle() = mediaExposer.observeShuffle()

    override fun observeQueue(): Flow<List<PlayerItem>> = mediaExposer.observeQueue()

    private fun mediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    private fun transportControls(): MediaControllerCompat.TransportControls? {
        return mediaController()?.transportControls
    }

    override fun playFromMediaId(mediaId: MediaId, filter: String?) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_FILTER to filter
        )

        transportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun playMostPlayed(mediaId: MediaId) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_MEDIA_ID to mediaId.toString()
        )
        transportControls()?.sendCustomAction(
            MusicServiceCustomAction.PLAY_MOST_PLAYED.name,
            bundle
        )
    }

    override fun playRecentlyAdded(mediaId: MediaId) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_MEDIA_ID to mediaId.toString()
        )
        transportControls()?.sendCustomAction(
            MusicServiceCustomAction.PLAY_RECENTLY_ADDED.name,
            bundle
        )
    }

    override fun skipToQueueItem(idInPlaylist: Int) {
        transportControls()?.skipToQueueItem(idInPlaylist.toLong())
    }

    override fun shuffle(mediaId: MediaId, filter: String?) {
        transportControls()?.sendCustomAction(
            MusicServiceCustomAction.SHUFFLE.name, bundleOf(
                MusicServiceCustomAction.ARGUMENT_MEDIA_ID to mediaId.toString(),
                MusicServiceCustomAction.ARGUMENT_FILTER to filter
            )
        )
    }

    override fun skipToNext() {
        transportControls()?.skipToNext()
    }

    override fun skipToPrevious() {
        transportControls()?.skipToPrevious()
    }

    override fun playPause() {
        mediaController()?.playPause()
    }

    override fun seekTo(where: Long) {
        transportControls()?.seekTo(where)
    }

    override fun toggleShuffleMode() {
        transportControls()?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_INVALID)
    }

    override fun toggleRepeatMode() {
        transportControls()?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_INVALID)
    }

    override fun togglePlayerFavorite() {
        transportControls()?.setRating(RatingCompat.newHeartRating(false))
    }

    override fun swap(from: Int, to: Int) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_SWAP_FROM to from,
            MusicServiceCustomAction.ARGUMENT_SWAP_TO to to
        )
        transportControls()?.sendCustomAction(MusicServiceCustomAction.SWAP.name, bundle)
    }

    override fun swapRelative(from: Int, to: Int) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_SWAP_FROM to from,
            MusicServiceCustomAction.ARGUMENT_SWAP_TO to to
        )
        transportControls()?.sendCustomAction(MusicServiceCustomAction.SWAP_RELATIVE.name, bundle)
    }

    override fun remove(position: Int) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_POSITION to position
        )
        transportControls()?.sendCustomAction(MusicServiceCustomAction.REMOVE.name, bundle)
    }

    override fun removeRelative(position: Int) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_POSITION to position
        )
        transportControls()?.sendCustomAction(MusicServiceCustomAction.REMOVE_RELATIVE.name, bundle)
    }

    override fun moveRelative(position: Int) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_POSITION to position
        )
        transportControls()?.sendCustomAction(MusicServiceCustomAction.MOVE_RELATIVE.name, bundle)
    }

    override fun addToPlayNext(mediaId: MediaId) {
        transportControls()?.sendCustomAction(
            MusicServiceCustomAction.ADD_TO_PLAY_NEXT.name,
            bundleOf(
                MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST to longArrayOf(mediaId.leaf!!),
                MusicServiceCustomAction.ARGUMENT_IS_PODCAST to mediaId.isAnyPodcast
            )

        )
    }

    override fun replayTenSeconds() {
        transportControls()?.sendCustomAction(MusicServiceCustomAction.REPLAY_10.name, null)
    }

    override fun forwardTenSeconds() {
        transportControls()?.sendCustomAction(MusicServiceCustomAction.FORWARD_10.name, null)
    }

    override fun replayThirtySeconds() {
        transportControls()?.sendCustomAction(MusicServiceCustomAction.REPLAY_30.name, null)
    }

    override fun forwardThirtySeconds() {
        transportControls()?.sendCustomAction(MusicServiceCustomAction.FORWARD_30.name, null)
    }
}