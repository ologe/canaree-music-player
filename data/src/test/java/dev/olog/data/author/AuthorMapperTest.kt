package dev.olog.data.author

import dev.olog.core.entity.id.AuthorIdentifier
import dev.olog.core.author.Artist
import org.junit.Assert
import org.junit.Test

class AuthorMapperTest {

    @Test
    fun `test artists view toDomain`() {
        val actual = Artists_view(
            id = 1,
            name = "name",
            songs = 2,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Artist(
            id = AuthorIdentifier.MediaStore(1, false),
            name = "name",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test podcast author view toDomain`() {
        val actual = Podcast_authors_view(
            id = 1,
            name = "name",
            episodes = 2,
            dateAdded = 100,
            directory = "dir"
        ).toDomain()

        val expected = Artist(
            id = AuthorIdentifier.MediaStore(1, true),
            name = "name",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

}