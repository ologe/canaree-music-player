package dev.olog.shared_android.music_service

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.reactivex.Flowable

interface IRxMusicServiceControllerCallback {

    fun registerCallback(controller: MediaControllerCompat)
    fun unregisterCallback(controller: MediaControllerCompat)

    fun onPlaybackStateChanged(): Flowable<PlaybackStateCompat>
    fun onMetadataChanged(): Flowable<MediaMetadataCompat>
    fun onRepeatModeChanged(): Flowable<Int>
    fun onShuffleModeChanged(): Flowable<Int>
    fun onExtrasChanged(): Flowable<Bundle>
}