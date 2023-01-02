package dev.olog.feature.media.api.connection

interface IMediaConnectionCallback {
    fun onConnectionStateChanged(state: MusicServiceConnectionState)
}