package dev.olog.shared_android.music_service

import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class RxMusicServiceConnectionCallback @Inject constructor() : IRxMusicServiceConnectionCallback {

    private val connectionCallback = ConnectionCallback()
    private val connectionPublisher = BehaviorSubject.createDefault(MusicServiceConnectionState.NONE)

    override fun onConnectionChanged(): Observable<MusicServiceConnectionState> {
        return connectionPublisher
    }

    override fun get(): ConnectionCallback {
        return connectionCallback
    }

    override fun setState(state: MusicServiceConnectionState) {
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
