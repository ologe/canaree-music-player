package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class SongTest {

    @Test
    fun testTrackGetMediaId() {
        val id = 1L
        val song = Mocks.song.copy(id = id)
        assertEquals(
            MediaId.playableItem(MediaId.createCategoryValue(MediaIdCategory.SONGS, ""), id),
            song.getMediaId()
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 1L
        val album = Mocks.podcast.copy(id = id)
        assertEquals(
            MediaId.playableItem(MediaId.createCategoryValue(MediaIdCategory.PODCASTS, ""), id),
            album.getMediaId()
        )
    }

    @Test
    fun testTrackGetArtistMediaId() {
        val id = 1L
        val album = Mocks.song.copy(artistId = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.ARTISTS, id.toString()),
            album.getArtistMediaId()
        )
    }

    @Test
    fun testPodcastGetArtistMediaId() {
        val id = 1L
        val album = Mocks.podcast.copy(artistId = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.PODCASTS_AUTHORS, id.toString()),
            album.getArtistMediaId()
        )
    }

    @Test
    fun testTrackGetAlbumMediaId() {
        val id = 1L
        val album = Mocks.song.copy(albumId = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.ALBUMS, id.toString()),
            album.getAlbumMediaId()
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