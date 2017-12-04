package dev.olog.presentation.service_music

import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RxMusicServiceConnectionCallback @Inject constructor() {

    private val connectionCallback = ConnectionCallback()
    private val connectionPublisher = BehaviorSubject.createDefault(MusicServiceConnectionState.NONE)

    fun onConnectionChanged(): Observable<MusicServiceConnectionState> {
        return connectionPublisher
    }

    fun get(): ConnectionCallback {
        return connectionCallback
    }

    fun setState(state: MusicServiceConnectionState) {
        connectionPublisher.onNext(state)
    }

    inner class ConnectionCallback : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            Log.d("connection callback", "Service connected")
            connectionPublisher.onNext(MusicServiceConnectionState.CONNECTING)
        }

        override fun onConnectionSuspended() {
            Log.e("connection callback", "The Service has crashed")
            connectionPublisher.onNext(MusicServiceConnectionState.FAILED)
        }

        override fun onConnectionFailed() {
            Log.w("connection callback", "The Service has refused connection")
            connectionPublisher.onNext(MusicServiceConnectionState.FAILED)
        }

    }

}
