package dev.olog.msc.floating.window.service.music.service

import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.base.music.service.MusicServiceConnectionState
import dev.olog.msc.presentation.widget.image.view.toPlayerImage
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@PerService
class MusicServiceBinder @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle

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

    fun onStateChanged(): Observable<PlaybackStateCompat> {
        return statePublisher.observeOn(Schedulers.computation())
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

    fun skipToNext() {
        mediaController?.transportControls?.skipToNext()
    }

    fun skipToPrevious() {
        mediaController?.transportControls?.skipToPrevious()
    }

    val animatePlayPauseLiveData: Observable<Int> = statePublisher
            .filter { it.isPlaying() || it.isPaused() }
            .map { it.state }
            .distinctUntilChanged()

    val onBookmarkChangedLiveData: Observable<Long> = statePublisher
            .filter { it.isPlaying() || it.isPaused() }
            .map { it.position }

    val onMetadataChanged : Observable<MusicServiceMetadata> = metadataPublisher
            .map { MusicServiceMetadata(it.getId(), it.getTitle().toString(),
                    it.getArtist().toString(), it.toPlayerImage(), it.getDuration(),
                    it.isPodcast()
            ) }

    val onMaxChangedLiveData: Observable<Long> = metadataPublisher
            .map { it.getDuration() }

}