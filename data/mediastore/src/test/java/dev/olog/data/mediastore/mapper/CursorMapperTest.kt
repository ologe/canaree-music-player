package dev.olog.data.mediastore.mapper

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.data.mediastore.queries.Columns
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Genre
import dev.olog.domain.entity.track.Track
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.milliseconds

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CursorMapperTest {

    private fun buildCursor(map: Map<String, Any?>): Cursor {
        val sorted = map.toSortedMap()
        val cursor = MatrixCursor(sorted.keys.toTypedArray())

        val firstRow = sorted.mapValues { (_, value) ->
            if (value is List<Any?>?) value.orEmpty().first() else value
        }
        cursor.addRow(firstRow.values)

        val withMoreValues = map
            .filter { (_, value) -> value is List<Any?>? && (value.orEmpty().size  > 1) }
            .mapValues { it.value as List<Any?>? }
            .mapValues { it.value.orEmpty().drop(1) }

        for ((key, variants) in withMoreValues) {
            for (variant in variants) {
                val firstRowCopy = firstRow.toMutableMap()
                firstRowCopy[key] = variant
                cursor.addRow(firstRowCopy.toSortedMap().values)
            }
        }

        return cursor
    }

    @Test
    fun `map to song`() {
        val cursor = buildCursor(mapOf(
                MediaStore.Audio.AudioColumns._ID to 1,
                MediaStore.Audio.AudioColumns.ARTIST_ID to 2,
                MediaStore.Audio.AudioColumns.ALBUM_ID to 3,
                MediaStore.Audio.AudioColumns.TITLE to "title",
                MediaStore.Audio.AudioColumns.ARTIST to "artist",
                MediaStore.Audio.AudioColumns.ALBUM to "album",
                Columns.ALBUM_ARTIST to listOf(
                    "album_artist",
                    null
                ),
                MediaStore.Audio.AudioColumns.DURATION to 123,
                MediaStore.Audio.AudioColumns.DATE_ADDED to 456,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED to 789,
                MediaStore.Audio.AudioColumns.DATA to "data",
                MediaStore.Audio.AudioColumns.TRACK to 4001,
                MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                    0,
                    1
                ),
        ))

        // default
        cursor.moveToNext()
        val expectedDefault = Track.Song(
            id = 1,
            artistId = 2,
            albumId = 3,
            title = "title",
            artist = "artist",
            albumArtist = "album_artist",
            album = "album",
            duration = 123.milliseconds,
            dateModified = 789,
            dateAdded = 456,
            path = "data",
            trackColumn = 4001,
            isPodcast = false
        )

        Assertions.assertEquals(expectedDefault, cursor.toTrack())

        // null album artist
        cursor.moveToNext()
        val expectedNoAlbumArtist = expectedDefault.copy(
            albumArtist = expectedDefault.artist
        )

        Assertions.assertEquals(expectedNoAlbumArtist, cursor.toTrack())

        // podcast
        cursor.moveToNext()
        val expectedPodcast = expectedDefault.copy(
            isPodcast = true
        )

        Assertions.assertEquals(expectedPodcast, cursor.toTrack())

        cursor.close()
    }

    @Test
    fun `map to playlist songs`() {
        val playlistId = 5L

        val cursor = buildCursor(mapOf(
            MediaStore.Audio.Playlists.Members._ID to 1,
            MediaStore.Audio.Playlists.Members.AUDIO_ID to 2,
            MediaStore.Audio.AudioColumns.ARTIST_ID to 3,
            MediaStore.Audio.AudioColumns.ALBUM_ID to 4,
            MediaStore.Audio.AudioColumns.TITLE to "title",
            MediaStore.Audio.AudioColumns.ARTIST to "artist",
            MediaStore.Audio.AudioColumns.ALBUM to "album",
            Columns.ALBUM_ARTIST to listOf(
                "album_artist",
                null
            ),
            MediaStore.Audio.AudioColumns.DURATION to 123,
            MediaStore.Audio.AudioColumns.DATE_ADDED to 456,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED to 789,
            MediaStore.Audio.AudioColumns.DATA to "data",
            MediaStore.Audio.AudioColumns.TRACK to 4001,
            MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                0,
                1
            ),
        ))

        // default
        cursor.moveToNext()
        val expectedDefault = Track.PlaylistSong(
            id = 2,
            artistId = 3,
            albumId = 4,
            title = "title",
            artist = "artist",
            albumArtist = "album_artist",
            album = "album",
            duration = 123.milliseconds,
            dateModified = 789,
            dateAdded = 456,
            path = "data",
            trackColumn = 4001,
            isPodcast = false,
            playlistId = playlistId,
            idInPlaylist = 1,
        )

        Assertions.assertEquals(expectedDefault, cursor.toPlaylistTrack(playlistId))

        // null album artist
        cursor.moveToNext()
        val expectedNoAlbumArtist = expectedDefault.copy(
            albumArtist = expectedDefault.artist
        )

        Assertions.assertEquals(expectedNoAlbumArtist, cursor.toPlaylistTrack(playlistId))

        // podcast
        cursor.moveToNext()
        val expectedPodcast = expectedDefault.copy(
            isPodcast = true
        )

        Assertions.assertEquals(expectedPodcast, cursor.toPlaylistTrack(playlistId))

        cursor.close()
    }


    @Test
    fun `map to album`() {
        val cursor = buildCursor(mapOf(
            MediaStore.Audio.Media.ALBUM_ID to 1,
            MediaStore.Audio.Media.ARTIST_ID to 2,
            MediaStore.Audio.Media.ALBUM to "album",
            MediaStore.Audio.Media.ARTIST to "artist",
            Columns.ALBUM_ARTIST to listOf(
                "album_artist",
                null,
            ),
            MediaStore.Audio.AudioColumns.DATA to listOf(
                "/storage/emulated/0/music/track.mp3",
                "/storage/emulated/0/album/track.mp3",
                "",
            ),
            MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                0,
                1
            ),
        ))

        // default
        cursor.moveToNext()
        val expectedDefault = Album(
            id = 1,
            artistId = 2,
            title = "album",
            artist = "artist",
            albumArtist = "album_artist",
            songs = 0,
            hasSameNameAsFolder = false,
            isPodcast = false
        )

        Assertions.assertEquals(expectedDefault, cursor.toAlbum())

        // null album artist
        cursor.moveToNext()
        val expectedNoAlbumArtist = expectedDefault.copy(
            albumArtist = expectedDefault.artist
        )
        Assertions.assertEquals(expectedNoAlbumArtist, cursor.toAlbum())

        // same name as folder
        cursor.moveToNext()
        val expectedSameNameAsFolder = expectedDefault.copy(
            hasSameNameAsFolder = true
        )
        Assertions.assertEquals(expectedSameNameAsFolder, cursor.toAlbum())

        // null, name different than folder
        cursor.moveToNext()
        val expectedDifferentNameThanFolder = expectedDefault.copy(
            hasSameNameAsFolder = false
        )
        Assertions.assertEquals(expectedDifferentNameThanFolder, cursor.toAlbum())

        // podcast
        cursor.moveToNext()
        val expectedPodcast = expectedDefault.copy(
            isPodcast = true
        )
        Assertions.assertEquals(expectedPodcast, cursor.toAlbum())

        cursor.close()
    }


    @Test
    fun `map to artist`() {
        val cursor = buildCursor(mapOf(
            MediaStore.Audio.Media.ARTIST_ID to 1,
            MediaStore.Audio.Media.ARTIST to "name",
            MediaStore.Audio.Media.ALBUM_ARTIST to listOf(
                "album_artist",
                null
            ),
            MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                0,
                1
            ),
        ))

        // default
        cursor.moveToNext()
        val expectedDefault = Artist(
            id = 1,
            name = "name",
            albumArtist = "album_artist",
            songs = 0,
            isPodcast = false
        )

        Assertions.assertEquals(expectedDefault, cursor.toArtist())

        // null album artist
        cursor.moveToNext()
        val expectedNoAlbumArtist = expectedDefault.copy(
            albumArtist = expectedDefault.name
        )
        Assertions.assertEquals(expectedNoAlbumArtist, cursor.toArtist())

        // podcast
        cursor.moveToNext()
        val expectedPodcast = expectedDefault.copy(
            isPodcast = true
        )
        Assertions.assertEquals(expectedPodcast, cursor.toArtist())

        cursor.close()
    }

    @Test
    fun `map to genre`() = runBlockingTest {
        val size = 123

        val cursor = buildCursor(mapOf(
            BaseColumns._ID to 1,
            MediaStore.Audio.GenresColumns.NAME to "name",
        ))

        // default
        cursor.moveToNext()
        val expectedDefault = Genre(
            id = 1,
            name = "Name",
            size = size
        )

        Assertions.assertEquals(expectedDefault, cursor.toGenre { size })

        cursor.close()
    }

}