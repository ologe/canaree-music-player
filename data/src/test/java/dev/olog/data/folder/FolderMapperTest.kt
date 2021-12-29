package dev.olog.data.folder

import dev.olog.core.MediaUri
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.folder.Folder
import dev.olog.core.track.Song
import org.junit.Assert
import org.junit.Test

class FolderMapperTest {

    @Test
    fun `test folder toDomain`() {
        val actual = Folders_view(
            directory = "dir",
            songs = 2,
            date_added = 100
        ).toDomain()
        val expected = Folder(
            uri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Folder,
                id = "dir",
                isPodcast = false
            ),
            directory = "dir",
            songs = 2
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test SelectMostPlayed toDomain`() {
        val actual = SelectMostPlayed(
            id = "1",
            author_id = "2",
            collection_id = "3",
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
                uri = MediaUri(
                    source = MediaUri.Source.MediaStore,
                    category = MediaUri.Category.Track,
                    id = "1",
                    isPodcast = false,
                ),
                artistUri = MediaUri(
                    source = MediaUri.Source.MediaStore,
                    category = MediaUri.Category.Author,
                    id = "2",
                    isPodcast = false,
                ),
                albumUri = MediaUri(
                    source = MediaUri.Source.MediaStore,
                    category = MediaUri.Category.Collection,
                    id = "3",
                    isPodcast = false,
                ),
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
                idInPlaylist = 0,
            ),
            counter = 100,
        )

        Assert.assertEquals(expected, actual)
    }

}