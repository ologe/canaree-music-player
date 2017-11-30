package dev.olog.presentation.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.olog.shared.ApplicationContext
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceBinder @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaBrowser: MediaBrowserCompat,
        private val connectionCallback: RxMusicServiceConnectionCallback,
        private val mediaControllerCallback: RxMusicServiceControllerCallback

) : Observer<MusicServiceConnectionState>, DefaultLifecycleObserver {

    private lateinit var connectionDisposable: Disposable

    private val mediaControllerLiveData = MutableLiveData<MediaControllerCompat>()
    private var mediaController: MediaControllerCompat? = null

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        connectionCallback.onConnectionChanged()
                .subscribe(this)

        if (!mediaBrowser.isConnected){
            mediaBrowser.connect()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        connectionCallback.setState(MusicServiceConnectionState.NONE)
        if (mediaController != null) {
            mediaControllerCallback.unregisterCallback(mediaController!!)
        }

        mediaControllerLiveData.value = null

        connectionDisposable.unsubscribe()
        mediaBrowser.disconnect()
    }

    fun getMediaControllerLiveData() = mediaControllerLiveData

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
            mediaControllerLiveData.value = mediaController
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
        }
        mediaControllerLiveData.value = null
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onConnectionFailed()
    }

    fun onConnectedObservable(): Observable<MediaBrowserCompat> {
        return connectionCallback.onConnectionChanged()
                .filter { it == MusicServiceConnectionState.CONNECTED }
                .map { mediaBrowser }
    }

    override fun onComplete() {}


}