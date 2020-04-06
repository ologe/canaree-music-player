package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.domain.MediaId.Companion.SONGS_CATEGORY
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class SongTest {

    @Test
    fun testGetMediaId() {
        val id = 10L
        val song = Mocks.song.copy(id = id)
        assertEquals(
            SONGS_CATEGORY.playableItem(id),
            song.mediaId
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 10L
        val album = Mocks.podcast.copy(id = id)
        assertEquals(
            PODCAST_CATEGORY.playableItem(id),
            album.mediaId
        )
    }

    @Test
    fun testGetArtistMediaId() {
        val id = 10L
        val album = Mocks.song.copy(artistId = id)
        assertEquals(
            Category(ARTISTS, id),
            album.artistMediaId
        )
    }

    @Test
    fun testPodcastGetArtistMediaId() {
        val id = 10L
        val album = Mocks.podcast.copy(artistId = id)
        assertEquals(
            Category(PODCASTS_AUTHORS, id),
            album.artistMediaId
        )
    }

    @Test
    fun testGetAlbumMediaId() {
        val id = 10L
        val album = Mocks.song.copy(albumId = id)
        assertEquals(
            Category(ALBUMS, id),
            album.albumMediaId
        )
    }

    @Test
    fun testDiscAndTrackNumber() {
        val song1 = Mocks.song.copy(trackColumn = 10)
        assertEquals(0, song1.discNumber)
        assertEquals(10, song1.trackNumber)

        val song2 = Mocks.song.copy(trackColumn = 1020)
        assertEquals(1, song2.discNumber)
        assertEquals(20, song2.trackNumber)

        val song3 = Mocks.song.copy(trackColumn = 0)
        assertEquals(0, song3.discNumber)
        assertEquals(0, song3.trackNumber)
    }

    @Test
    fun testFolderPath() {
        val song = Mocks.song.copy(path = "/storage/emulated/0/folder/item.mp3")
        assertEquals("/storage/emulated/0/folder", song.folderPath)
    }

    @Test
    fun testTrackParentMediaId() {
        val id = 2L
        val song = Mocks.song.copy(id = id, isPodcast = false)
        assertEquals(
            SONGS_CATEGORY,
            song.parentMediaId
        )
    }

    @Test
    fun testPodcastParentMediaId() {
        val id = 2L
        val song = Mocks.song.copy(id = id, isPodcast = true)
        assertEquals(
            PODCAST_CATEGORY,
            song.parentMediaId
        )
    }

}