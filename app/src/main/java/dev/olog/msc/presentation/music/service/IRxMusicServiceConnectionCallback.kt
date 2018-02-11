package dev.olog.shared_android.music_service

import io.reactivex.Observable

interface IRxMusicServiceConnectionCallback {

    fun get(): RxMusicServiceConnectionCallback.ConnectionCallback
    fun setState(state: MusicServiceConnectionState)
    fun onConnectionChanged(): Observable<MusicServiceConnectionState>

}