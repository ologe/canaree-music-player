package dev.olog.msc.floating.window.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.dagger.PerService
import dev.olog.msc.dagger.ServiceLifecycle
import dev.olog.msc.domain.interactor.music.service.ToggleSkipToNextVisibilityUseCase
import dev.olog.msc.domain.interactor.music.service.ToggleSkipToPreviousVisibilityUseCase
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.shared_android.Constants
import dev.olog.shared_android.music_service.IRxMusicServiceConnectionCallback
import dev.olog.shared_android.music_service.IRxMusicServiceControllerCallback
import dev.olog.shared_android.music_service.MusicServiceConnectionState
import io.reactivex.Flowable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerService
class MusicServiceBinder @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private var mediaBrowser: MediaBrowserCompat,
        private var connectionCallback: IRxMusicServiceConnectionCallback,
        private var mediaControllerCallback: IRxMusicServiceControllerCallback,
        toggleSkipToPreviousVisibilityUseCase: ToggleSkipToPreviousVisibilityUseCase,
        toggleSkipToNextVisibilityUseCase: ToggleSkipToNextVisibilityUseCase

) : Observer<MusicServiceConnectionState>, DefaultLifecycleObserver {

    private lateinit var connectionDisposable: Disposable

    private var mediaController: MediaControllerCompat? = null

    init {
        lifecycle.addObserver(this)
        connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disconnect()
    }

    private fun connect(){
        connectionCallback.onConnectionChanged()
                .subscribe(this)

        this.mediaBrowser.connect()
    }

    private fun disconnect(){
        this.mediaBrowser.disconnect()
        connectionDisposable.unsubscribe()
    }

    override fun onSubscribe(d: Disposable) {
        connectionDisposable = d
    }

    override fun onNext(connectionState: MusicServiceConnectionState) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (connectionState) {
            MusicServiceConnectionState.CONNECTING -> tryConnection()
            MusicServiceConnectionState.FAILED -> onConnectionFailed()
        }
    }

    private fun tryConnection() {
        try {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
            mediaControllerCallback.registerCallback(mediaController!!)
            onConnectionSuccessful()
        } catch (e: RemoteException) {
            e.printStackTrace()
            onConnectionFailed()
        }
    }

    private fun onConnectionSuccessful() {
        connectionCallback.setState(MusicServiceConnectionState.CONNECTED)
    }

    private fun onConnectionFailed() {
        if (mediaController != null) {
            mediaControllerCallback.unregisterCallback(mediaController!!)
            mediaController = null
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onConnectionFailed()
    }

    override fun onComplete() {}

    fun next(){
        mediaController?.transportControls?.skipToNext()
    }

    fun previous(){
        mediaController?.transportControls?.skipToPrevious()
    }

    fun playPause(){
        val playbackState = mediaController?.playbackState
        playbackState?.let {
            if (it.state == PlaybackStateCompat.STATE_PLAYING){
                mediaController?.transportControls?.pause()
            } else {
                mediaController?.transportControls?.play()
            }
        }
    }

    fun seekTo(progress: Long){
        mediaController?.transportControls?.seekTo(progress)
    }

    val animatePlayPauseLiveData: Flowable<Int> = mediaControllerCallback
            .onPlaybackStateChanged()
            .map { it.state }
            .filter { state -> state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED }
            .distinctUntilChanged()
            .skip(1)

    val skipToNextVisibility = toggleSkipToNextVisibilityUseCase.observe()
    val skipToPreviousVisibility = toggleSkipToPreviousVisibilityUseCase.observe()

    val animateSkipToLiveData: Flowable<Boolean> = mediaControllerCallback
            .onPlaybackStateChanged()
            .map { it.state }
            .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
            .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }

    val onMetadataChanged : Flowable<Pair<String, String>> = mediaControllerCallback
            .onMetadataChanged()
            .map {
                var artist = it.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                if (artist == Constants.UNKNOWN){
                    artist = Constants.UNKNOWN_ARTIST
                }

                it.getString(MediaMetadataCompat.METADATA_KEY_TITLE) to artist
            }

    val onBookmarkChangedLiveData: Flowable<Long> = mediaControllerCallback.onPlaybackStateChanged()
            .filter { playbackState ->
                val state = playbackState.state
                state == PlaybackStateCompat.STATE_PAUSED || state == PlaybackStateCompat.STATE_PLAYING
            }.map { it.position }

    val onMaxChangedLiveData: Flowable<Long> = mediaControllerCallback
            .onMetadataChanged()
            .map { metadata -> metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) }

}