package dev.olog.lib.media.model

import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerPlaybackStateTest {

    @Test
    fun test() {
        val state = STATE_PLAYING
        val position = 100L
        val speed = 0.5f

        val playbackState = Builder()
            .setState(state, position, speed)
            .build()

        val sut = PlayerPlaybackState(playbackState)

        assertEquals(PlayerState.PLAYING, sut.state)
        assertEquals(position.toInt(), sut.bookmark)
        assertEquals(speed, sut.playbackSpeed)
        assertTrue(sut.isPlaying)
        assertFalse(sut.isPaused)
        assertTrue(sut.isPlayOrPause)
        assertFalse(sut.isSkipTo)
    }

    @Test
    fun `test is paused`() {
        val position = 123L
        val playbackState = Builder()
            .setState(STATE_PAUSED, position, 0f)
            .build()

        val sut = PlayerPlaybackState(playbackState)

        assertTrue(sut.isPaused)
        assertTrue(sut.isPlayOrPause)
        assertFalse(sut.isPlaying)
        assertEquals(position.toInt(), sut.bookmark)
    }

    @Test
    fun `test is skip to next`() {
        val playbackState = Builder()
            .setState(STATE_SKIPPING_TO_NEXT, 0L, 0f)
            .build()

        val sut = PlayerPlaybackState(playbackState)

        assertTrue(sut.isSkipTo)
    }

    @Test
    fun `test is skip to previous`() {
        val playbackState = Builder()
            .setState(STATE_SKIPPING_TO_PREVIOUS, 0L, 0f)
            .build()

        val sut = PlayerPlaybackState(playbackState)

        assertTrue(sut.isSkipTo)
    }

    @Test
    fun `test equals and hashcode`() {
        val state = STATE_PLAYING
        val position = 100L
        val speed = 0.5f

        val playbackState = PlaybackStateCompat.Builder()
            .setState(state, position, speed)
            .build()

        val sut1 = PlayerPlaybackState(playbackState)
        val sut2 = PlayerPlaybackState(playbackState)

        // check same instance
        assertTrue(sut1 == sut1)
        // check equality
        assertTrue(sut1 == sut2)
        assertTrue(sut1.hashCode() == sut2.hashCode())
    }

}