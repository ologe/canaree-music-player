package dev.olog.data.collection

import dev.olog.core.MediaUri
import dev.olog.core.collection.Album
import org.junit.Assert
import org.junit.Test

class CollectionMapperTest {

    @Test
    fun `test albums view toDomain`() {
        val actual = Albums_view(
            id = "1",
            author_id = "2",
            title = "title",
            author = "author",
            songs = 3,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Album(
            uri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Collection,
                id = "1",
                isPodcast = false
            ),
            artistUri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Author,
                id = "2",
                isPodcast = false
            ),
            title = "title",
            artist = "author",
            songs = 3,
            directory = "dir",
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test podcast collection view toDomain`() {
        val actual = Podcast_collections_view(
            id = "1",
            author_id = "2",
            title = "title",
            author = "author",
            songs = 3,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Album(
            uri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Collection,
                id = "1",
                isPodcast = true
            ),
            artistUri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Author,
                id = "2",
                isPodcast = true
            ),
            title = "title",
            artist = "author",
            songs = 3,
            directory = "dir",
        )

        Assert.assertEquals(expected, actual)
    }

}