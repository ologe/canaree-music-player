package dev.olog.data.genre

import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.id.AuthorIdentifier
import dev.olog.core.entity.id.CollectionIdentifier
import dev.olog.core.entity.id.GenreIdentifier
import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.core.author.Artist
import dev.olog.core.genre.Genre
import dev.olog.core.playable.Song
import dev.olog.testing.GenreView
import org.junit.Assert
import org.junit.Test

class GenreMapperTest {

    @Test
    fun `test genre toDomain`() {
        val actual = GenreView(
            id = 1,
            name = "name",
            2
        ).toDomain()

        val expected = Genre(
            id = GenreIdentifier.MediaStore(1),
            name = "name",
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

    @Test
    fun `test SelectRelatedArtists toDomain`() {
        val actual = SelectRelatedArtists(
            author_id = 1,
            author = "author",
            album_artist = "album_artist",
            songs = 2
        ).toDomain()

        val expected = Artist(
            id = AuthorIdentifier.MediaStore(1, false),
            name = "author",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test genre playables view toDomain`() {
        val actual = Genres_playables_view(
            genre_id = 1,
            id = 2,
            author_id = 3,
            collection_id = 4,
            title = "title",
            author = "author",
            album_artist = "album_artist",
            collection = "collection",
            duration = 5,
            date_added = 6,
            directory = "directory",
            path = "path",
            disc_number = 7,
            track_number = 8,
            is_podcast = false
        ).toDomain()

        val expected = Song(
            id = PlayableIdentifier.MediaStore(2, false),
            artistId = AuthorIdentifier.MediaStore(3, false),
            albumId = CollectionIdentifier.MediaStore(4, false),
            title = "title",
            artist = "author",
            albumArtist = "album_artist",
            album = "collection",
            duration = 5,
            dateAdded = 6,
            directory = "directory",
            path = "path",
            discNumber = 7,
            trackNumber = 8,
            idInPlaylist = 0,
        )

        Assert.assertEquals(expected, actual)
    }

}