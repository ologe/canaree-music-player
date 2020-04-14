package dev.olog.lib.media.model

import android.support.v4.media.MediaMetadataCompat.*
import dev.olog.domain.MediaId
import dev.olog.core.constants.MusicConstants.IS_PODCAST
import dev.olog.core.constants.MusicConstants.PATH
import dev.olog.core.constants.MusicConstants.SKIP_NEXT
import dev.olog.core.constants.MusicConstants.SKIP_PREVIOUS
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.putBoolean
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlayerMetadataTest {

    @Test
    fun `test non boolean values`() {
        // given
        val id = 1L
        val mediaId = MediaId.SONGS_CATEGORY.playableItem(id)
        val title = "title"
        val artist = "artist"
        val album = "album"
        val duration = 123456L
        val path = "/storage/emulated"

        val metadata = Builder()
            .putString(METADATA_KEY_MEDIA_ID, mediaId.toString())
            .putString(METADATA_KEY_TITLE, title)
            .putString(METADATA_KEY_ARTIST, artist)
            .putString(METADATA_KEY_ALBUM, album)
            .putLong(METADATA_KEY_DURATION, duration)
            .putString(PATH, path)
            .build()

        val sut = PlayerMetadata(metadata)

        assertEquals(id, sut.id)
        assertEquals(mediaId, sut.mediaId)
        assertEquals(title, sut.title)
        assertEquals(artist, sut.artist)
        assertEquals(album, sut.album)
        assertEquals(duration, sut.duration)
        assertEquals(path, sut.path)
        assertEquals(TextUtils.formatMillis(sut.duration), sut.readableDuration)
    }

    @Test
    fun `test isPodcast`() {
        val metadata = Builder()
            .putBoolean(IS_PODCAST, true)
            .build()

        val sut = PlayerMetadata(metadata)

        assertTrue(sut.isPodcast)
    }

    @Test
    fun `test skip to next`() {
        val metadata = Builder()
            .putBoolean(SKIP_NEXT, true)
            .build()

        val sut = PlayerMetadata(metadata)

        assertTrue(sut.isSkippingToNext)
    }

    @Test
    fun `test skip to previous`() {
        val metadata = Builder()
            .putBoolean(SKIP_PREVIOUS, true)
            .build()

        val sut = PlayerMetadata(metadata)

        assertTrue(sut.isSkippingToPrevious)
    }

    @Test
    fun `test equals and hashcode`() {
        val id = 1L
        val mediaId = MediaId.SONGS_CATEGORY.playableItem(id)
        val title = "title"
        val artist = "artist"
        val album = "album"
        val duration = 123456L
        val path = "/storage/emulated"

        val metadata = Builder()
            .putString(METADATA_KEY_MEDIA_ID, mediaId.toString())
            .putString(METADATA_KEY_TITLE, title)
            .putString(METADATA_KEY_ARTIST, artist)
            .putString(METADATA_KEY_ALBUM, album)
            .putLong(METADATA_KEY_DURATION, duration)
            .putBoolean(IS_PODCAST, true)
            .putBoolean(SKIP_NEXT, true)
            .putBoolean(SKIP_PREVIOUS, true)
            .putString(PATH, path)
            .build()

        val sut1 = PlayerMetadata(metadata)
        val sut2 = PlayerMetadata(metadata)

        // check same instance
        assertTrue(sut1 == sut1)
        // check equality
        assertTrue(sut1 == sut2)
        assertTrue(sut1.hashCode() == sut2.hashCode())
    }

}