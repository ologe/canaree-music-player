package dev.olog.data.folder

import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.id.AuthorIdentifier
import dev.olog.core.entity.id.CollectionIdentifier
import dev.olog.core.entity.id.FolderIdentifier
import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.core.folder.Folder
import dev.olog.core.playable.Song
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
            id = FolderIdentifier.Path("dir"),
            directory = "dir",
            songs = 2
        )
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
                id = PlayableIdentifier.MediaStore(1, false),
                artistId = AuthorIdentifier.MediaStore(2, false),
                albumId = CollectionIdentifier.MediaStore(3, false),
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