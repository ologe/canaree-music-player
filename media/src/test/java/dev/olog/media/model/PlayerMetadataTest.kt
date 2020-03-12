package dev.olog.media.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.core.MediaId
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerMetadataTest {

    @Test
    fun testMediaId() {
        // given
        val mediaId = MediaId.SHUFFLE_ID

        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId.toString())
            .build()

        val sut = PlayerMetadata(metadata)

        // when
        val actual = sut.mediaId

        assertEquals(
            mediaId,
            actual
        )
    }

}