package dev.olog.presentation.service_music

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.dagger.PerActivity
import dev.olog.shared.ApplicationContext
import dev.olog.shared.unsubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerActivity
class MusicServiceBinderViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        @ActivityLifecycle private val lifecycle: Lifecycle,
        private var mediaBrowser: MediaBrowserCompat,
        private var connectionCallback: RxMusicServiceConnectionCallback,
        private var mediaControllerCallback: RxMusicServiceControllerCallback

) : Observer<MusicServiceConnectionState>, DefaultLifecycleObserver {

    private lateinit var connectionDisposable: Disposable

    private val mediaControllerLiveData = MutableLiveData<MediaControllerCompat>()
    private var mediaController: MediaControllerCompat? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        connectionCallback.onConnectionChanged()
                .subscribe(this)

        this.mediaBrowser.connect()
    }

    override fun onStop(owner: LifecycleOwner) {
        mediaBrowser.disconnect()

        connectionCallback.setState(MusicServiceConnectionState.NONE)
        if (mediaController != null) {
            mediaControllerCallback.unregisterCallback(mediaController!!)
            this.mediaController = null
        }

        mediaControllerLiveData.value = null

        connectionDisposable.unsubscribe()
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
            mediaController = null
        }
        mediaControllerLiveData.value = null
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onConnectionFailed()
    }

    override fun onComplete() {}


}