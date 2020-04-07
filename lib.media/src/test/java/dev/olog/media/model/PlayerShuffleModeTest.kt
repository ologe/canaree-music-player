package dev.olog.media.model

import android.support.v4.media.session.PlaybackStateCompat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerShuffleModeTest {

    @Test
    fun `test none`() {
        assertEquals(
            PlayerShuffleMode.DISABLED,
            PlayerShuffleMode.of(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        )
    }

    @Test
    fun `test one`() {
        assertEquals(
            PlayerShuffleMode.ENABLED,
            PlayerShuffleMode.of(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        )
    }

    @Test
    fun `test invalid`() {
        assertEquals(
            PlayerShuffleMode.DISABLED,
            PlayerShuffleMode.of(PlaybackStateCompat.SHUFFLE_MODE_INVALID)
        )
    }

    @Test
    fun `test group`() {
        assertEquals(
            PlayerShuffleMode.DISABLED,
            PlayerShuffleMode.of(PlaybackStateCompat.SHUFFLE_MODE_GROUP)
        )
    }

    @Test
    fun `test random int`() {
        assertEquals(
            PlayerShuffleMode.DISABLED,
            PlayerShuffleMode.of(-1235)
        )
    }

}