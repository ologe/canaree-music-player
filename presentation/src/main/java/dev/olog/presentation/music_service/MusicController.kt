package dev.olog.presentation.music_service

import android.media.session.PlaybackState.STATE_PAUSED
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.presentation.dagger.PerActivity
import dev.olog.shared.MusicConstants.ACTION_PLAY_SHUFFLE
import javax.inject.Inject

@PerActivity
class MusicController @Inject constructor(
        private val mediaControllerProvider: MediaControllerProvider
) {


    fun playPause() {
        val transportControls = getTransportControls() ?: return

        getMediaController()?.playbackState?.let {
            val state = it.state
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                transportControls.pause()
            } else if (state == STATE_PAUSED) {
                transportControls.play()
            }
        }
    }

    fun skipToNext() {
        getTransportControls()?.skipToNext()
    }

    fun skipToPrevious() {
        getTransportControls()?.skipToPrevious()
    }

    fun toggleRepeatMode() {
        getTransportControls()?.setRepeatMode(-1)
    }

    fun toggleShuffleMode() {
        getTransportControls()?.setShuffleMode(-1)
    }


    fun seekTo(pos: Long){
        getTransportControls()?.seekTo(pos)
    }

    fun playFromMediaId(mediaId: String) {
        getTransportControls()?.playFromMediaId(mediaId, null)
    }

    fun playShuffle(mediaId: String) {
        val bundle = Bundle()
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
        getTransportControls()?.sendCustomAction(ACTION_PLAY_SHUFFLE, bundle)
    }

    private fun getTransportControls(): MediaControllerCompat.TransportControls? {
        return mediaControllerProvider.getSupportMediaController()?.transportControls
    }

    private fun getMediaController(): MediaControllerCompat? {
        return mediaControllerProvider.getSupportMediaController()
    }

}