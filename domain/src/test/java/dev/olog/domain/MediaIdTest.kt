package dev.olog.domain

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.domain.MediaId.Companion.SHUFFLE_ID
import dev.olog.domain.MediaId.Companion.SONGS_CATEGORY
import dev.olog.domain.MediaId.Track
import dev.olog.domain.MediaIdCategory.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MediaIdTest {

    @Test
    fun `test special media id are different`() {
        val ids = setOf(PODCAST_CATEGORY, SHUFFLE_ID, SONGS_CATEGORY)
        assertEquals(3, ids.size)
    }

    @Test
    fun `test media track parent id`() {
        val category = ALBUMS
        val categoryId = 1L
        val id = 2L
        val mediaId = Track(category, categoryId, id)

        val expected = Category(category, categoryId)

        assertEquals(
            expected,
            mediaId.parentId
        )
    }

    @Test
    fun `test category fromString`() {
        val string = "ALBUMS/1"
        val expected = Category(ALBUMS, 1)
        assertEquals(
            expected,
            MediaId.fromString(string)
        )
    }

    @Test
    fun `test track fromString`() {
        val string = "ALBUMS/1|2"
        val expected = Track(ALBUMS, 1, 2)
        assertEquals(
            expected,
            MediaId.fromString(string)
        )
    }

    @Test(expected = Exception::class)
    fun `test empty fromString, should crash`() {
        val string = "ALBUMS|1|2"
        MediaId.fromString(string)
    }

    @Test(expected = Exception::class)
    fun `test invalid fromString, should crash`() {
        val string = "ALBUMS|1|2"
        MediaId.fromString(string)
    }

    @Test
    fun `test isAnyPodcast`() {
        val podcast = listOf(PODCASTS_PLAYLIST, PODCASTS, PODCASTS_AUTHORS)

        for (value in values()) {
            val actual = Category(value, 1)
            assertEquals(
                value in podcast,
                actual.isAnyPodcast
            )
        }
    }

    @Test
    fun `test category toString`() {
        val album = Category(ALBUMS, 1)
        assertEquals(
            "ALBUMS/1",
            album.toString()
        )
    }

    @Test
    fun `test track toString`() {
        val album = Track(ALBUMS, 1, 2)
        assertEquals(
            "ALBUMS/1|2",
            album.toString()
        )
    }

    @Test
    fun `test category equals and hashcode is implemented`() {
        val category1 = Category(ALBUMS, 1)
        val category2 = Category(ALBUMS, 1)
        assertFalse(category1 === category2)
        assertEquals(category1, category2)
    }

    @Test
    fun `test track equals and hashcode is implemented`() {
        val track1 = Track(ALBUMS, 1, 2)
        val track2 = Track(ALBUMS, 1, 2)
        assertFalse(track1 === track2)
        assertEquals(track1, track2)
    }

}