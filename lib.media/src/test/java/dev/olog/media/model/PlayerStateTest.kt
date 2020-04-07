package dev.olog.media.model

import android.support.v4.media.session.PlaybackStateCompat
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerStateTest {

    @Test
    fun `test play`() {
        assertEquals(
            PlayerState.PLAYING,
            PlayerState.of(PlaybackStateCompat.STATE_PLAYING)
        )
    }

    @Test
    fun `test pause`() {
        assertEquals(
            PlayerState.PAUSED,
            PlayerState.of(PlaybackStateCompat.STATE_PAUSED)
        )
    }

    @Test
    fun `test skip next`() {
        assertEquals(
            PlayerState.SKIP_TO_NEXT,
            PlayerState.of(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
        )
    }

    @Test
    fun `test skip previous`() {
        assertEquals(
            PlayerState.SKIP_TO_PREVIOUS,
            PlayerState.of(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
        )
    }

    @Test
    fun `test not allowed states`() {
        val allStates = listOf(
            PlaybackStateCompat.STATE_NONE,
            PlaybackStateCompat.STATE_STOPPED,
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.STATE_FAST_FORWARDING,
            PlaybackStateCompat.STATE_REWINDING,
            PlaybackStateCompat.STATE_BUFFERING,
            PlaybackStateCompat.STATE_ERROR,
            PlaybackStateCompat.STATE_CONNECTING,
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT,
            PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM
        )

        val allowed = listOf(
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
        )

        allowed.toTypedArray()
        for (state in allStates) {
            if (state in allowed) {
                continue
            }
            try {
                PlayerState.of(state)
                Assert.fail("only $allowed is allow, instead was $state")
            } catch (ex: IllegalArgumentException) {

            }
        }
    }

}