package dev.olog.lib.media.connection

internal interface IMediaConnectionCallback {
    fun onConnectionStateChanged(state: MusicServiceConnectionState)
}