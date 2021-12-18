package dev.olog.data.utils

import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.data.index.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContentResolverExtensionsTest {

    @Test
    fun `test mapToIndexedPlayables`() {
        val cursor = MatrixCursor(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            Columns.ALBUM_ARTIST,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATE_ADDED,
            MediaStore.Audio.AudioColumns.TRACK,
            MediaStore.Audio.AudioColumns.IS_PODCAST,
        )

        // valid
        cursor.newRow(
            MediaStore.Audio.AudioColumns._ID to 1L,
            MediaStore.Audio.AudioColumns.ARTIST_ID to 2L,
            MediaStore.Audio.AudioColumns.ALBUM_ID to 3L,
            MediaStore.Audio.AudioColumns.DATA to "/storage/emulated/playable.mp3",
            MediaStore.Audio.AudioColumns.TITLE to "title",
            MediaStore.Audio.AudioColumns.ARTIST to "artist",
            MediaStore.Audio.AudioColumns.ALBUM to "album",
            Columns.ALBUM_ARTIST to "album_artist",
            MediaStore.Audio.AudioColumns.DURATION to 4L,
            MediaStore.Audio.AudioColumns.DATE_ADDED to 5L,
            MediaStore.Audio.AudioColumns.TRACK to 2001L,
            MediaStore.Audio.AudioColumns.IS_PODCAST to 1,
        )

        // valid
        cursor.newRow(
            MediaStore.Audio.AudioColumns._ID to 10L,
            MediaStore.Audio.AudioColumns.ARTIST_ID to 20L,
            MediaStore.Audio.AudioColumns.ALBUM_ID to 30L,
            MediaStore.Audio.AudioColumns.DATA to "/emulated/playable.mp3",
            MediaStore.Audio.AudioColumns.TITLE to "another title",
            MediaStore.Audio.AudioColumns.ARTIST to "another artist",
            MediaStore.Audio.AudioColumns.ALBUM to "another album",
            Columns.ALBUM_ARTIST to "another album_artist",
            MediaStore.Audio.AudioColumns.DURATION to 40L,
            MediaStore.Audio.AudioColumns.DATE_ADDED to 50L,
            MediaStore.Audio.AudioColumns.TRACK to 10L,
            MediaStore.Audio.AudioColumns.IS_PODCAST to 0,
        )

        // valid, empty path
        cursor.newRow(
            MediaStore.Audio.AudioColumns._ID to 100L,
            MediaStore.Audio.AudioColumns.ARTIST_ID to 200L,
            MediaStore.Audio.AudioColumns.ALBUM_ID to 300L,
            MediaStore.Audio.AudioColumns.DATA to "",
            MediaStore.Audio.AudioColumns.TITLE to "another title 2",
            MediaStore.Audio.AudioColumns.ARTIST to "another artist 2",
            MediaStore.Audio.AudioColumns.ALBUM to "another album 2",
            Columns.ALBUM_ARTIST to "another album_artist 2",
            MediaStore.Audio.AudioColumns.DURATION to 400L,
            MediaStore.Audio.AudioColumns.DATE_ADDED to 500L,
            MediaStore.Audio.AudioColumns.TRACK to 11020L,
            MediaStore.Audio.AudioColumns.IS_PODCAST to 0,
        )

        // no id, should be skipped
        cursor.newRow(
            MediaStore.Audio.AudioColumns._ID to null,
            MediaStore.Audio.AudioColumns.DATA to "used later",
            MediaStore.Audio.AudioColumns.ARTIST to "used later",
            MediaStore.Audio.AudioColumns.TRACK to -1,
        )

        // no artist id, should be skipped
        cursor.newRow(
            MediaStore.Audio.AudioColumns._ID to 1L,
            MediaStore.Audio.AudioColumns.ARTIST_ID to null,
            MediaStore.Audio.AudioColumns.DATA to "used later",
            MediaStore.Audio.AudioColumns.ARTIST to "used later",
            MediaStore.Audio.AudioColumns.TRACK to -1,
        )

        // no album id, should be skipped
        cursor.newRow(
            MediaStore.Audio.AudioColumns._ID to 1L,
            MediaStore.Audio.AudioColumns.ARTIST_ID to 2L,
            MediaStore.Audio.AudioColumns.ALBUM_ID to null,
            MediaStore.Audio.AudioColumns.DATA to "used later",
            MediaStore.Audio.AudioColumns.ARTIST to "used later",
            MediaStore.Audio.AudioColumns.TRACK to -1,
        )

        val actual = cursor.mapToIndexedPlayables()
        val expected = listOf(
            Indexed_playables(
                id = 1L,
                author_id = 2L,
                collection_id = 3L,
                title = "title",
                author = "artist",
                album_artist = "album_artist",
                collection = "album",
                duration = 4L,
                date_added = 5,
                directory = "/storage/emulated",
                path = "/storage/emulated/playable.mp3",
                disc_number = 2,
                track_number = 1,
                is_podcast = true,
            ),
            Indexed_playables(
                id = 10L,
                author_id = 20L,
                collection_id = 30L,
                title = "another title",
                author = "another artist",
                album_artist = "another album_artist",
                collection = "another album",
                duration = 40L,
                date_added = 50,
                directory = "/emulated",
                path = "/emulated/playable.mp3",
                disc_number = 0,
                track_number = 10,
                is_podcast = false,
            ),
            Indexed_playables(
                id = 100L,
                author_id = 200L,
                collection_id = 300L,
                title = "another title 2",
                author = "another artist 2",
                album_artist = "another album_artist 2",
                collection = "another album 2",
                duration = 400L,
                date_added = 500,
                directory = "",
                path = "",
                disc_number = 11,
                track_number = 20,
                is_podcast = false,
            ),
        )

        Assert.assertEquals(expected, actual)

        cursor.close()
    }

    @Test
    fun `test mapToIndexedGenres`() {
        val cursor = MatrixCursor(
            BaseColumns._ID,
            MediaStore.Audio.GenresColumns.NAME
        )

        // valid
        cursor.newRow(
            BaseColumns._ID to 1L,
            MediaStore.Audio.GenresColumns.NAME to "Some genre 1"
        )

        // valid, should be capitalized
        cursor.newRow(
            BaseColumns._ID to 2L,
            MediaStore.Audio.GenresColumns.NAME to "some genre 2"
        )

        // missing id, should be skipped
        cursor.newRow(
            BaseColumns._ID to null,
            MediaStore.Audio.GenresColumns.NAME to "genre 3"
        )
        // missing name, should be skipped
        cursor.newRow(
            BaseColumns._ID to 3L,
            MediaStore.Audio.GenresColumns.NAME to null
        )

        val actual = cursor.mapToIndexedGenres()
        val expected = listOf(
            Indexed_genres(
                id = 1,
                name = "Some genre 1"
            ),
            Indexed_genres(
                id = 2,
                name = "Some genre 2"
            ),
        )
        Assert.assertEquals(expected, actual)

        cursor.close()
    }

    @Test
    fun `test mapToIndexedGenrePlayable`() {
        val cursor = MatrixCursor(
            MediaStore.Audio.Genres.Members.AUDIO_ID
        )

        // valid
        cursor.newRow(
            MediaStore.Audio.Genres.Members.AUDIO_ID to 1
        )
        // valid
        cursor.newRow(
            MediaStore.Audio.Genres.Members.AUDIO_ID to 2
        )
        // null id, , should be skipped
        cursor.newRow(
            MediaStore.Audio.Genres.Members.AUDIO_ID to null
        )

        val actual = cursor.mapToIndexedGenrePlayable(10)
        val expected = listOf(
            Indexed_genres_playables(10, 1),
            Indexed_genres_playables(10, 2),
        )

        Assert.assertEquals(expected, actual)

        cursor.close()
    }

    @Test
    fun `test mapToIndexedPlaylist`() {
        val cursor = MatrixCursor(
            BaseColumns._ID,
            MediaStore.Audio.Playlists.NAME,
            MediaStore.Audio.Playlists.DATA,
        )

        // valid
        cursor.newRow(
            BaseColumns._ID to 1L,
            MediaStore.Audio.Playlists.NAME to "Some playlist 1",
            MediaStore.Audio.Playlists.DATA to "path 1",
        )

        // valid, should be capitalized
        cursor.newRow(
            BaseColumns._ID to 2L,
            MediaStore.Audio.Playlists.NAME to "some playlist 2",
            MediaStore.Audio.Playlists.DATA to "path 2",
        )

        // missing id, should be skipped
        cursor.newRow(
            BaseColumns._ID to null,
            MediaStore.Audio.Playlists.NAME to "playlist 3",
            MediaStore.Audio.Playlists.DATA to "path 3",
        )
        // missing name, should be skipped
        cursor.newRow(
            BaseColumns._ID to 3L,
            MediaStore.Audio.Playlists.NAME to null,
            MediaStore.Audio.Playlists.DATA to "path 4",
        )

        val actual = cursor.mapToIndexedPlaylist()
        val expected = listOf(
            Indexed_playlists(
                id = 1,
                title = "Some playlist 1",
                path = "path 1"
            ),
            Indexed_playlists(
                id = 2,
                title = "Some playlist 2",
                path = "path 2"
            ),
        )
        Assert.assertEquals(expected, actual)

        cursor.close()
    }

    @Test
    fun `test mapToIndexedPlaylistPlayable`() {
        val cursor = MatrixCursor(
            BaseColumns._ID,
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER,
        )

        // valid
        cursor.newRow(
            BaseColumns._ID to 1,
            MediaStore.Audio.Playlists.Members.AUDIO_ID to 10,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER to 100,
        )
        // valid
        cursor.newRow(
            BaseColumns._ID to 2,
            MediaStore.Audio.Playlists.Members.AUDIO_ID to 20,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER to 200,
        )
        // null id, should be skipped
        cursor.newRow(
            BaseColumns._ID to null,
            MediaStore.Audio.Playlists.Members.AUDIO_ID to 30
        )
        // null audio id, should be skipped
        cursor.newRow(
            BaseColumns._ID to 4,
            MediaStore.Audio.Playlists.Members.AUDIO_ID to null
        )
        // null play order, should be skipped
        cursor.newRow(
            BaseColumns._ID to 5,
            MediaStore.Audio.Playlists.Members.AUDIO_ID to 50,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER to null,
        )

        val actual = cursor.mapToIndexedPlaylistPlayable(1000)
        val expected = listOf(
            Indexed_playlists_playables(id = 1, playlist_id = 1000, playable_id = 10, play_order = 100),
            Indexed_playlists_playables(id = 2, playlist_id = 1000, playable_id = 20, play_order = 200),
        )

        Assert.assertEquals(expected, actual)

        cursor.close()
    }

}