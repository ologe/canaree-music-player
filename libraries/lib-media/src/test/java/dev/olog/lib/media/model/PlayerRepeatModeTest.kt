package dev.olog.lib.media.model

import android.support.v4.media.session.PlaybackStateCompat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerRepeatModeTest {

    @Test
    fun `test none`() {
        assertEquals(
            PlayerRepeatMode.NONE,
            PlayerRepeatMode.of(PlaybackStateCompat.REPEAT_MODE_NONE)
        )
    }

    @Test
    fun `test one`() {
        assertEquals(
            PlayerRepeatMode.ONE,
            PlayerRepeatMode.of(PlaybackStateCompat.REPEAT_MODE_ONE)
        )
    }

    @Test
    fun `test all`() {
        assertEquals(
            PlayerRepeatMode.ALL,
            PlayerRepeatMode.of(PlaybackStateCompat.REPEAT_MODE_ALL)
        )
    }

    @Test
    fun `test invalid`() {
        assertEquals(
            PlayerRepeatMode.NONE,
            PlayerRepeatMode.of(PlaybackStateCompat.REPEAT_MODE_INVALID)
        )
    }

    @Test
    fun `test group`() {
        assertEquals(
            PlayerRepeatMode.NONE,
            PlayerRepeatMode.of(PlaybackStateCompat.REPEAT_MODE_GROUP)
        )
    }

    @Test
    fun `test random int`() {
        assertEquals(
            PlayerRepeatMode.NONE,
            PlayerRepeatMode.of(-1235)
        )
    }

}