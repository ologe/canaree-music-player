package dev.olog.music_service.helper

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.music_service.R
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.shared.ApplicationContext
import javax.inject.Inject

class MusixmatchPlugin @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val mediaSession: MediaSessionCompat

) : DefaultLifecycleObserver {

    private val callback = object : MediaControllerCompat.Callback(){
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (metadata != null){
                updateMetadata(metadata, mediaSession.controller.playbackState)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state != null){
//                updatePlaybackState(state, mediaSession.controller.metadata) todo too many updates
            }
        }
    }

    init {
        mediaSession.controller.registerCallback(callback)
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaSession.controller.unregisterCallback(callback)
    }



    private fun updateMetadata(metadata: MediaMetadataCompat, playbackState: PlaybackStateCompat){
        val intent = Intent()
        intent.action = "com.android.music.metachanged"
        val bundle = BundleBuilder(context)
                .setTitle(metadata)
                .setArtist(metadata)
                .setAlbum(metadata)
                .setDuration(metadata)
                .setPosition(playbackState)
                .isPlaying(playbackState)
                .build()
        intent.putExtras(bundle)
        context.sendBroadcast(intent)
    }

    private fun updatePlaybackState(playbackState: PlaybackStateCompat, metadata: MediaMetadataCompat){
        val intent = Intent()
        intent.action = "com.android.music.playstatechanged"
        val bundle = BundleBuilder(context)
                .setTitle(metadata)
                .setArtist(metadata)
                .setAlbum(metadata)
                .setDuration(metadata)
                .setPosition(playbackState)
                .isPlaying(playbackState)
                .build()
        intent.putExtras(bundle)
        context.sendBroadcast(intent)
    }

    private class BundleBuilder(
            private val context: Context
    ) {

        private val bundle = Bundle()

        fun setTitle(metadata: MediaMetadataCompat): BundleBuilder{
            val title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            bundle.putString("track", title)
            return this
        }

        fun setArtist(metadata: MediaMetadataCompat): BundleBuilder{
            val artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
            if (artist == context.getString(R.string.unknown_artist)){
                bundle.putString("artist", "<unknown>")
            } else {
                bundle.putString("artist", artist)
            }
            return this
        }

        fun setAlbum(metadata: MediaMetadataCompat): BundleBuilder{
            val album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
            if (album == context.getString(R.string.unknown_album)){
                bundle.putString("album", "<unknown>")
            } else {
                bundle.putString("album", album)
            }
            return this
        }

        fun isPlaying(playbackState: PlaybackStateCompat): BundleBuilder{
            val isPlaying = playbackState.playbackState == PlaybackStateCompat.STATE_PLAYING
            bundle.putBoolean("playing", isPlaying)
            return this
        }

        fun setDuration(metadata: MediaMetadataCompat): BundleBuilder{
            val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            bundle.putLong("duration", duration)
            return this
        }

        fun setPosition(playbackState: PlaybackStateCompat): BundleBuilder{
            val position = playbackState.position
            bundle.putLong("position", position)
            return this
        }

        fun build(): Bundle {
            bundle.putString("scrobbling_source", context.packageName)
            return bundle
        }

    }

}