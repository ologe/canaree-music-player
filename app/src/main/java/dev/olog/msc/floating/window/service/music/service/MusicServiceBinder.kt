package dev.olog.msc.floating.window.service.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.base.music.service.MusicServiceConnectionState
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@PerService
class MusicServiceBinder @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        musicPrefsUseCase: MusicPreferencesUseCase

) : DefaultLifecycleObserver {

    private val mediaBrowser = MediaBrowserCompat(context, ComponentName(context, MusicService::class.java),
            FloatingMusicConnection(this), null)
    private val connectionDisposable: Disposable
    private val publisher = BehaviorSubject.createDefault(MusicServiceConnectionState.NONE)

    internal val metadataPublisher = BehaviorSubject.create<MediaMetadataCompat>()
    internal val statePublisher = BehaviorSubject.create<PlaybackStateCompat>()
    internal val repeatModePublisher = BehaviorSubject.create<Int>()
    internal val shuffleModePublisher = BehaviorSubject.create<Int>()
    internal val queuePublisher = BehaviorSubject.createDefault(mutableListOf<MediaSessionCompat.QueueItem>())

    private var mediaController: MediaControllerCompat? = null
    private val callback = FloatingMusicCallback(this)

    init {
        lifecycle.addObserver(this)
        connectionDisposable = publisher.subscribe({
            when (it){
                MusicServiceConnectionState.CONNECTED -> onConnected()
                MusicServiceConnectionState.FAILED -> onConnectionFailed()
                else -> {}
            }
        }, Throwable::printStackTrace)
        mediaBrowser.connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaController?.unregisterCallback(callback)
        connectionDisposable.unsubscribe()
        this.mediaBrowser.disconnect()
    }

    private fun onConnected() {
        try {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
            mediaController!!.registerCallback(callback)
            initialize(mediaController!!)
        } catch (e: RemoteException) {
            e.printStackTrace()
            onConnectionFailed()
        }
    }

    private fun onConnectionFailed(){
        mediaController?.unregisterCallback(callback)
    }

    internal fun updateConnectionState(state: MusicServiceConnectionState){
        publisher.onNext(state)
    }

    private fun initialize(mediaController : MediaControllerCompat){
        callback.onMetadataChanged(mediaController.metadata)
        callback.onPlaybackStateChanged(mediaController.playbackState)
        callback.onRepeatModeChanged(mediaController.repeatMode)
        callback.onShuffleModeChanged(mediaController.shuffleMode)
        callback.onQueueChanged(mediaController.queue)
    }

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

    val animatePlayPauseLiveData: Observable<Int> = statePublisher
            .filter { it.isPlaying() || it.isPaused() }
            .map { it.state }
            .distinctUntilChanged()
            .skip(1)

    val skipToNextVisibility = musicPrefsUseCase.observeSkipToNextVisibility()
    val skipToPreviousVisibility = musicPrefsUseCase.observeSkipToPreviousVisibility()

    val animateSkipToLiveData: Observable<Boolean> = statePublisher
            .map { it.state }
            .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
            .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }

    val onBookmarkChangedLiveData: Observable<Long> = statePublisher
            .filter { it.isPlaying() || it.isPaused() }
            .map { it.position }

    val onMetadataChanged : Observable<Pair<String, String>> = metadataPublisher
            .map {
                var artist = it.getArtist().toString()
                if (artist == AppConstants.UNKNOWN){
                    artist = AppConstants.UNKNOWN_ARTIST
                }

                it.getString(MediaMetadataCompat.METADATA_KEY_TITLE) to artist
            }

    val onMaxChangedLiveData: Observable<Long> = metadataPublisher
            .map { it.getDuration() }

}