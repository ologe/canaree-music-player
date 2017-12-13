package dev.olog.presentation.service_music

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.olog.shared.unsubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MusicServiceBinderViewModel(
        application: Application

) : AndroidViewModel(application), Observer<MusicServiceConnectionState> {

    private lateinit var connectionDisposable: Disposable

    private val mediaControllerLiveData = MutableLiveData<MediaControllerCompat>()
    private var mediaController: MediaControllerCompat? = null

    private var mediaBrowser: MediaBrowserCompat? = null
    private var connectionCallback: RxMusicServiceConnectionCallback? = null
    private var mediaControllerCallback: RxMusicServiceControllerCallback? = null

    fun connect(mediaBrowserCompat: MediaBrowserCompat,
                connectionCallback: RxMusicServiceConnectionCallback,
                mediaControllerCallback: RxMusicServiceControllerCallback){

        this.mediaBrowser = mediaBrowserCompat
        this.connectionCallback = connectionCallback
        this.mediaControllerCallback = mediaControllerCallback

        connectionCallback.onConnectionChanged()
                .subscribe(this)

        if (!this.mediaBrowser!!.isConnected){
            this.mediaBrowser!!.connect()
        }
    }

    fun disconnect(){
        connectionCallback!!.setState(MusicServiceConnectionState.NONE)
        if (mediaController != null) {
            mediaControllerCallback!!.unregisterCallback(mediaController!!)
        }

        mediaControllerLiveData.value = null

        connectionDisposable.unsubscribe()
        if (mediaBrowser!!.isConnected){
            mediaBrowser!!.disconnect()
        }

        this.mediaBrowser = null
        this.connectionCallback = null
        this.mediaControllerCallback = null
        this.mediaController = null
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
            mediaController = MediaControllerCompat(getApplication(), mediaBrowser!!.sessionToken)
            mediaControllerLiveData.value = mediaController
            mediaControllerCallback!!.registerCallback(mediaController!!)

            onConnectionSuccessful()
        } catch (e: RemoteException) {
            e.printStackTrace()
            onConnectionFailed()
        }

    }

    private fun onConnectionSuccessful() {
        connectionCallback!!.setState(MusicServiceConnectionState.CONNECTED)
    }

    private fun onConnectionFailed() {
        if (mediaController != null) {
            mediaControllerCallback!!.unregisterCallback(mediaController!!)
        }
        mediaControllerLiveData.value = null
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onConnectionFailed()
    }

    override fun onComplete() {}


}