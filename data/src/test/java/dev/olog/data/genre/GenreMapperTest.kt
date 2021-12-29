package dev.olog.data.genre

import dev.olog.core.MediaStoreGenreUri
import dev.olog.core.MediaUri
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.author.Artist
import dev.olog.core.genre.Genre
import dev.olog.core.track.Song
import dev.olog.testing.GenreView
import org.junit.Assert
import org.junit.Test

class GenreMapperTest {

    @Test
    fun `test genre toDomain`() {
        val actual = GenreView(
            id = "1",
            name = "name",
            2
        ).toDomain()

        val expected = Genre(
            uri = MediaStoreGenreUri(1),
            name = "name",
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

    @Test
    fun `test SelectRelatedArtists toDomain`() {
        val actual = SelectRelatedArtists(
            author_id = "1",
            author = "author",
            album_artist = "album_artist",
            songs = 2
        ).toDomain()

        val expected = Artist(
            uri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Author,
                id = "1",
                isPodcast = false
            ),
            name = "author",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test genre playables view toDomain`() {
        val actual = Genres_playables_view(
            genre_id = "1",
            id = "2",
            author_id = "3",
            collection_id = "4",
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
            uri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Track,
                id = "2",
                isPodcast = false,
            ),
            artistUri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Author,
                id = "3",
                isPodcast = false,
            ),
            albumUri = MediaUri(
                source = MediaUri.Source.MediaStore,
                category = MediaUri.Category.Collection,
                id = "4",
                isPodcast = false,
            ),
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