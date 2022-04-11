package dev.olog.presentation.main

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.intents.MusicServiceAction
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.media.MediaExposer
import dev.olog.media.MediaProvider
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.model.*
import dev.olog.media.playPause
import dev.olog.presentation.base.BaseActivity
import dev.olog.shared.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow

abstract class MusicGlueActivity : BaseActivity(),
    MediaProvider,
    OnConnectionChanged,
    CoroutineScope by MainScope() {

    private val mediaExposer by lazyFast {
        MediaExposer(
            this,
            this
        )
    }

    fun connect() {
        mediaExposer.connect()

    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        connect()
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
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

    override fun observeMetadata(): LiveData<PlayerMetadata> = mediaExposer.observeMetadata()

    override fun observePlaybackState(): LiveData<PlayerPlaybackState> =
        mediaExposer.observePlaybackState()

    override fun observeRepeat(): LiveData<PlayerRepeatMode> = mediaExposer.observeRepeat()

    override fun observeShuffle(): LiveData<PlayerShuffleMode> = mediaExposer.observeShuffle()

    override fun observeQueue(): Flow<List<PlayerItem>> = mediaExposer.observeQueue()

    private fun mediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    private fun transportControls(): MediaControllerCompat.TransportControls? {
        return mediaController()?.transportControls
    }

    override fun playFromMediaId(mediaId: MediaId, filter: String?, sort: SortEntity?) {
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_FILTER to filter,
            MusicServiceAction.ARGUMENT_SORT_TYPE to sort?.type?.name,
            MusicServiceAction.ARGUMENT_SORT_ARRANGING to sort?.arranging?.name
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