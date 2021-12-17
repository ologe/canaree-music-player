package dev.olog.data.playable

import dev.olog.core.entity.track.Song
import dev.olog.data.index.Indexed_playables
import org.junit.Assert
import org.junit.Test

class PlayableMappersTest {

    @Test
    fun `test toDomain`() {
        val actual = Indexed_playables(
            id = 1,
            author_id = 2,
            collection_id = 3,
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
            id = 1,
            artistId = 2,
            albumId = 3,
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
            isPodcast = false,
            idInPlaylist = 0,
        )

        Assert.assertEquals(expected, actual)
    }

}