package dev.olog.media.controller

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

internal class MediaControllerCallbackTest {

    val callback = mock<IMediaControllerCallback>()
    val sut = MediaControllerCallback(callback)

    @Test
    fun `test metadata changed`() {
        val metadata = MediaMetadataCompat.Builder()
            .build()

        sut.onMetadataChanged(metadata)

        verify(callback).onMetadataChanged(metadata)
    }

    @Test
    fun `test playback state changed`() {
        val state = PlaybackStateCompat.Builder().build()

        sut.onPlaybackStateChanged(state)

        verify(callback).onPlaybackStateChanged(state)
    }

    @Test
    fun `test repeat mode changed`() {
        val mode = PlaybackStateCompat.REPEAT_MODE_ALL

        sut.onRepeatModeChanged(mode)

        verify(callback).onRepeatModeChanged(mode)
    }

    @Test
    fun `test shuffle mode changed`() {
        val mode = PlaybackStateCompat.SHUFFLE_MODE_ALL

        sut.onShuffleModeChanged(mode)

        verify(callback).onShuffleModeChanged(mode)
    }

    @Test
    fun `test queue changed`() {
        val queue = mutableListOf(
            MediaSessionCompat.QueueItem(
                MediaDescriptionCompat.Builder().build(),
                1
            )
        )

        sut.onQueueChanged(queue)

        verify(callback).onQueueChanged(queue)
    }

    @Test(expected = IllegalStateException::class)
    fun `test on title changed`() {
        sut.onQueueTitleChanged(null)
    }

    @Test(expected = IllegalStateException::class)
    fun `test on extras changed`() {
        sut.onExtrasChanged(null)
    }

}