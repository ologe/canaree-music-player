package dev.olog.feature.media.api.connection

internal interface IMediaConnectionCallback {
    fun onConnectionStateChanged(state: MusicServiceConnectionState)
}