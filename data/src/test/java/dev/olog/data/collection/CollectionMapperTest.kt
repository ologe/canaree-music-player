package dev.olog.data.collection

import dev.olog.core.entity.track.Album
import org.junit.Assert
import org.junit.Test

class CollectionMapperTest {

    @Test
    fun `test albums view toDomain`() {
        val actual = Albums_view(
            id = 1,
            author_id = 2,
            title = "title",
            author = "author",
            songs = 3,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Album(
            id = 1,
            artistId = 2,
            title = "title",
            artist = "author",
            songs = 3,
            directory = "dir",
            isPodcast = false,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test podcast collection view toDomain`() {
        val actual = Podcast_collections_view(
            id = 1,
            author_id = 2,
            title = "title",
            author = "author",
            songs = 3,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Album(
            id = 1,
            artistId = 2,
            title = "title",
            artist = "author",
            songs = 3,
            directory = "dir",
            isPodcast = true,
        )

        Assert.assertEquals(expected, actual)
    }

}