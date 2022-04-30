package dev.olog.feature.media.connection

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

internal class MusicServiceConnectionTest {

    val callback = mock<IMediaConnectionCallback>()
    val sut = MusicServiceConnection(callback)

    @Test
    fun `test connected`() {
        sut.onConnected()

        verify(callback).onConnectionStateChanged(MusicServiceConnectionState.CONNECTED)
    }

    @Test
    fun `test connection suspended`() {
        sut.onConnectionSuspended()

        verify(callback).onConnectionStateChanged(MusicServiceConnectionState.FAILED)
    }

    @Test
    fun `test connection failed`() {
        sut.onConnectionFailed()

        verify(callback).onConnectionStateChanged(MusicServiceConnectionState.FAILED)
    }

}