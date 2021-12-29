package dev.olog.data.playable

import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.data.index.Indexed_playables
import org.junit.Assert
import org.junit.Test

class PlayableMappersTest {

    @Test
    fun `test toDomain`() {
        val actual = Indexed_playables(
            id = "1",
            author_id = "2",
            collection_id = "3",
            title = "title",
            author = "author",
            album_artist = "album_artist",
            collection = "collection",
            duration = 10,
            date_added = 20,
            directory = "directory",
            path = "path",
            disc_number = 100,
            track_number = 200,
            is_podcast = false
        ).toDomain()

        val expected = Song(
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
            duration = 10,
            dateAdded = 20,
            directory = "directory",
            path = "path",
            discNumber = 100,
            trackNumber = 200,
            idInPlaylist = 0,
        )

        Assert.assertEquals(expected, actual)
    }

}