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
@Config(manifest = Config.NONE)
class CursorMapperTest {

    @Test
    fun `map to song`() = runBlockingTest {
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

        testCursor(
            expectedDefault = expectedDefault,
            mapper = Cursor::toTrack,
            map = mapOf(
                MediaStore.Audio.AudioColumns._ID to 1,
                MediaStore.Audio.AudioColumns.ARTIST_ID to 2,
                MediaStore.Audio.AudioColumns.ALBUM_ID to 3,
                MediaStore.Audio.AudioColumns.TITLE to "title",
                MediaStore.Audio.AudioColumns.ARTIST to "artist",
                MediaStore.Audio.AudioColumns.ALBUM to "album",
                Columns.ALBUM_ARTIST to listOf(
                    "album_artist",
                    null to expectedDefault.copy(albumArtist = expectedDefault.artist),
                ),
                MediaStore.Audio.AudioColumns.DURATION to 123,
                MediaStore.Audio.AudioColumns.DATE_ADDED to 456,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED to 789,
                MediaStore.Audio.AudioColumns.DATA to "data",
                MediaStore.Audio.AudioColumns.TRACK to 4001,
                MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                    0,
                    1 to expectedDefault.copy(isPodcast = true),
                ),
            ),
        )
    }

    @Test
    fun `map to playlist songs`() = runBlockingTest {
        val playlistId = 5L

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

        testCursor(
            expectedDefault = expectedDefault,
            mapper = { it.toPlaylistTrack(playlistId) },
            map = mapOf(
                MediaStore.Audio.Playlists.Members._ID to 1,
                MediaStore.Audio.Playlists.Members.AUDIO_ID to 2,
                MediaStore.Audio.AudioColumns.ARTIST_ID to 3,
                MediaStore.Audio.AudioColumns.ALBUM_ID to 4,
                MediaStore.Audio.AudioColumns.TITLE to "title",
                MediaStore.Audio.AudioColumns.ARTIST to "artist",
                MediaStore.Audio.AudioColumns.ALBUM to "album",
                Columns.ALBUM_ARTIST to listOf(
                    "album_artist",
                    null to expectedDefault.copy(albumArtist = expectedDefault.artist),
                ),
                MediaStore.Audio.AudioColumns.DURATION to 123,
                MediaStore.Audio.AudioColumns.DATE_ADDED to 456,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED to 789,
                MediaStore.Audio.AudioColumns.DATA to "data",
                MediaStore.Audio.AudioColumns.TRACK to 4001,
                MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                    0,
                    1 to expectedDefault.copy(isPodcast = true),
                ),
            ),
        )
    }


    @Test
    fun `map to album`() = runBlockingTest {
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

        testCursor(
            expectedDefault = expectedDefault,
            mapper = Cursor::toAlbum,
            mapOf(
                MediaStore.Audio.Media.ALBUM_ID to 1,
                MediaStore.Audio.Media.ARTIST_ID to 2,
                MediaStore.Audio.Media.ALBUM to "album",
                MediaStore.Audio.Media.ARTIST to "artist",
                Columns.ALBUM_ARTIST to listOf(
                    "album_artist",
                    null to expectedDefault.copy(albumArtist = expectedDefault.artist),
                ),
                MediaStore.Audio.AudioColumns.DATA to listOf(
                    "/storage/emulated/0/music/track.mp3",
                    "/storage/emulated/0/album/track.mp3" to expectedDefault.copy(hasSameNameAsFolder = true),
                    "" to expectedDefault.copy(hasSameNameAsFolder = false),
                ),
                MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                    0,
                    1 to expectedDefault.copy(isPodcast = true),
                ),
            ),
        )
    }


    @Test
    fun `map to artist`() = runBlockingTest {
        val expectedDefault = Artist(
            id = 1,
            name = "name",
            albumArtist = "album_artist",
            songs = 0,
            isPodcast = false
        )

        testCursor(
            expectedDefault = expectedDefault,
            mapper = Cursor::toArtist,
            map = mapOf(
                MediaStore.Audio.Media.ARTIST_ID to 1,
                MediaStore.Audio.Media.ARTIST to "name",
                MediaStore.Audio.Media.ALBUM_ARTIST to listOf(
                    "album_artist",
                    null to expectedDefault.copy(albumArtist = expectedDefault.name),
                ),
                MediaStore.Audio.AudioColumns.IS_PODCAST to listOf(
                    0,
                    1 to expectedDefault.copy(isPodcast = true),
                ),
            )
        )
    }

    @Test
    fun `map to genre, non null size, should return valid genre`() = runBlockingTest {
        val size = 123

        val expectedDefault = Genre(
            id = 1,
            name = "Name",
            size = size
        )

        testCursor(
            expectedDefault = expectedDefault,
            mapper = { it.toGenre { size } },
            map = mapOf(
                BaseColumns._ID to 1,
                MediaStore.Audio.GenresColumns.NAME to "name",
            )
        )
    }

    @Test
    fun `map to genre, null size, should return null genre`() = runBlockingTest {
        val size: Int? = null

        val expectedDefault: Genre? = null

        testCursor(
            expectedDefault = expectedDefault,
            mapper = { it.toGenre { size } },
            map = mapOf(
                BaseColumns._ID to 1,
                MediaStore.Audio.GenresColumns.NAME to "name",
            )
        )
    }

    /**
     * usage:
     *
     * class Test(val id: Int, val variant: String)
     *
     * val expectedDefault = Test(1, default)
     *
     * testCursor(
     *   expectedDefault = expectedDefault,
     *   mapper = some mapper that convert a Cursor to Test.class,
     *   map = mapOf(
     *      _id to 1,
     *      variant to listOf(
     *          "default",  // the first value of the list is used to build expectedDefault
     *          "variant_1" to expectedDefault.copy(variant = "variant_1"),  // pair.second is the expected state for this variant
     *          "variant_2" to expectedDefault.copy(variant = "variant_2"),
     *      )
     *   ),
     * )
     *
     * // note that if multiple columns has variants, each variant is computed using the
     * // default (first) value from the other columns
     */
    private suspend fun<T> testCursor(
        expectedDefault: T,
        mapper: suspend (Cursor) -> T,
        map: Map<String, Any?>,
    ) {
        val sorted = map.toSortedMap()
        val cursor = MatrixCursor(sorted.keys.toTypedArray())

        val firstRow = sorted.mapValues { (_, value) ->
            if (value is List<Any?>?) value.orEmpty().first() else value
        }
        cursor.addRow(firstRow.values)
        cursor.moveToFirst()
        Assertions.assertEquals(expectedDefault, mapper(cursor))

        val withMoreValues = map
            .filter { (_, value) -> value is List<Any?>? && (value.orEmpty().size  > 1) }
            .mapValues { it.value as List<Any?>? }
            .mapValues { it.value.orEmpty().drop(1) }

        for ((key, variants) in withMoreValues) {
            for (variantPair in variants) {
                require(variantPair is Pair<*, *>)
                val variant = variantPair.first
                val expectedVariant = variantPair.second

                val firstRowCopy = firstRow.toMutableMap()
                firstRowCopy[key] = variant
                cursor.addRow(firstRowCopy.toSortedMap().values)
                cursor.moveToNext()
                Assertions.assertEquals(expectedVariant, mapper(cursor))
            }
        }

        cursor.close()
    }

}