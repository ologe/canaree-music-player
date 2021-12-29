package dev.olog.data.author

import dev.olog.core.MediaUri
import dev.olog.core.author.Artist
import org.junit.Assert
import org.junit.Test

class AuthorMapperTest {

    @Test
    fun `test artists view toDomain`() {
        val actual = Artists_view(
            id = "1",
            name = "name",
            songs = 2,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Artist(
            uri = MediaUri(
                MediaUri.Source.MediaStore,
                MediaUri.Category.Author,
                "1",
                isPodcast = false,
            ),
            name = "name",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test podcast author view toDomain`() {
        val actual = Podcast_authors_view(
            id = "1",
            name = "name",
            episodes = 2,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Artist(
            uri = MediaUri(
                MediaUri.Source.MediaStore,
                MediaUri.Category.Author,
                "1",
                isPodcast = true,
            ),
            name = "name",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

}