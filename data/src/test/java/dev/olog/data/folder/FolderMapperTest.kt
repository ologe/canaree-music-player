package dev.olog.data.folder

import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import org.junit.Assert
import org.junit.Test

class FolderMapperTest {

    @Test
    fun `test folder toDomain`() {
        val actual = Folders_view("dir", 2, 100).toDomain()
        val expected = Folder("dir", 2)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test SelectMostPlayed toDomain`() {
        val actual = SelectMostPlayed(
            id = 1,
            author_id = 2,
            collection_id = 3,
            title = "title",
            author = "author",
            album_artist = "album_artist",
            collection = "collection",
            duration = 4,
            date_added = 5,
            directory = "directory",
            path = "path",
            disc_number = 6,
            track_number = 7,
            is_podcast = false,
            counter = 100
        ).toDomain()

        val expected = MostPlayedSong(
            song = Song(
                id = 1,
                artistId = 2,
                albumId = 3,
                title = "title",
                artist = "author",
                albumArtist = "album_artist",
                album = "collection",
                duration = 4,
                dateAdded = 5,
                directory = "directory",
                path = "path",
                discNumber = 6,
                trackNumber = 7,
                isPodcast = false,
                idInPlaylist = 0,
            ),
            counter = 100,
        )

        Assert.assertEquals(expected, actual)
    }

}