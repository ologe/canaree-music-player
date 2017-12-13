package dev.olog.presentation.service_music

import android.app.Application
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import dagger.Lazy
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.dagger.PerActivity
import dev.olog.shared.unsubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerActivity
class MusicServiceBinderViewModel @Inject constructor(
        private val application: Application,
        @ActivityLifecycle lifecycle: Lifecycle,
        private val view: Lazy<MediaControllerProvider>,
        private var mediaBrowser: MediaBrowserCompat,
        private var connectionCallback: RxMusicServiceConnectionCallback,
        private var mediaControllerCallback: RxMusicServiceControllerCallback

) : Observer<MusicServiceConnectionState>, DefaultLifecycleObserver {

    private lateinit var connectionDisposable: Disposable

    var mediaController: MediaControllerCompat? = null

    init {
        lifecycle.addObserver(this)
        connect()
    }

    private fun connect(){

        connectionCallback.onConnectionChanged()
                .subscribe(this)

        if (!this.mediaBrowser.isConnected){
            this.mediaBrowser.connect()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (mediaBrowser.isConnected){
            mediaBrowser.disconnect()
        }

        connectionCallback.setState(MusicServiceConnectionState.NONE)
        if (mediaController != null) {
            mediaControllerCallback.unregisterCallback(mediaController!!)
            this.mediaController = null
        }

        view.get().setSupportMediaController(null)

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
            mediaController = MediaControllerCompat(application, mediaBrowser.sessionToken)
            view.get().setSupportMediaController(mediaController)
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
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onConnectionFailed()
    }

    override fun onComplete() {}


}