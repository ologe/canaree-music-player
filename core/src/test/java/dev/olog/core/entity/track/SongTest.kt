package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.core.MediaId.Companion.SONGS_CATEGORY
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class SongTest {

    @Test
    fun testTrackGetMediaId() {
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
    fun testTrackGetArtistMediaId() {
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
    fun testTrackGetAlbumMediaId() {
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

}