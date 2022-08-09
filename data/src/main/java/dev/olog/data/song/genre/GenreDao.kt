package dev.olog.data.song.genre

import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Query
import dev.olog.data.DataConstants
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsView
import dev.olog.data.mediastore.song.genre.MediaStoreGenresView
import dev.olog.data.mediastore.song.genre.MediaStoreGenresViewSorted
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_GENRES_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ALBUM_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION
import dev.olog.data.sort.db.SORT_TYPE_TRACK_NUMBER
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
abstract class GenreDao {

    companion object {
        @Language("RoomSql")
        private const val SONGS_QUERY = """
SELECT songs_view.*
FROM mediastore_genre_track
    JOIN songs_view ON mediastore_genre_track.songId = songs_view.id
    LEFT JOIN sort ON TRUE
WHERE mediastore_genre_track.genreId = :id AND sort.tableName = '$SORT_TABLE_GENRES_SONGS'
ORDER BY
-- artist, then title
CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND album = '${MediaStore.UNKNOWN_STRING}' THEN -1 END,
CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(artist) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(artist) END COLLATE UNICODE DESC,
-- album, then title
CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND album = '${MediaStore.UNKNOWN_STRING}' THEN -1 END,
CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(album) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(album) END COLLATE UNICODE DESC,
-- album artist, then title
CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM_ARTIST' AND albumArtist = '${MediaStore.UNKNOWN_STRING}' THEN -1 END,
CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM_ARTIST' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(albumArtist) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM_ARTIST' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(albumArtist) END COLLATE UNICODE DESC,
-- duration, then title
CASE WHEN sort.columnName = '$SORT_TYPE_DURATION' AND sort.direction = '$SORT_DIRECTION_ASC' THEN duration END ASC,
CASE WHEN sort.columnName = '$SORT_TYPE_DURATION' AND sort.direction = '$SORT_DIRECTION_DESC' THEN duration END DESC,
-- date, then title
CASE WHEN sort.columnName = '$SORT_TYPE_DATE' AND sort.direction = '$SORT_DIRECTION_ASC' THEN dateAdded END DESC,
CASE WHEN sort.columnName = '$SORT_TYPE_DATE' AND sort.direction = '$SORT_DIRECTION_DESC' THEN dateAdded END ASC,
-- disc number, track number, then title
CASE WHEN sort.columnName = '$SORT_TYPE_TRACK_NUMBER' AND sort.direction = '$SORT_DIRECTION_ASC' THEN discNumber END ASC,
CASE WHEN sort.columnName = '$SORT_TYPE_TRACK_NUMBER' AND sort.direction = '$SORT_DIRECTION_ASC' THEN trackNumber END ASC,
CASE WHEN sort.columnName = '$SORT_TYPE_TRACK_NUMBER' AND sort.direction = '$SORT_DIRECTION_DESC' THEN discNumber END DESC,
CASE WHEN sort.columnName = '$SORT_TYPE_TRACK_NUMBER' AND sort.direction = '$SORT_DIRECTION_DESC' THEN trackNumber END DESC,
-- also, CASE WHEN sort.columnName = 'title'
CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(title) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(title) END COLLATE UNICODE DESC
"""
    }

    @Query("SELECT * from genres_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStoreGenresViewSorted>

    @Query("SELECT * from genres_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStoreGenresViewSorted>>

    @Query("SELECT * from genres_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStoreGenresView?

    @Query("SELECT * from genres_view WHERE id = :id")
    abstract fun observeById(id: String): Flow<MediaStoreGenresView?>

    @Query(SONGS_QUERY)
    // todo made suspend
    abstract fun getTracksById(id: String): List<MediaStoreSongsView>

    @Query(SONGS_QUERY)
    abstract fun observeTracksById(id: String): Flow<List<MediaStoreSongsView>>

    @Query("""
        SELECT * 
        FROM genres_view
        WHERE id != :id
        ORDER BY lower(name) COLLATE UNICODE ASC
    """)
    abstract fun observeSiblings(id: String): Flow<List<MediaStoreGenresView>>

    @Query("""
        SELECT songs_view.*
        FROM most_played_genre_v2 
            JOIN songs_view ON most_played_genre_v2.songId = songs_view.id
        WHERE most_played_genre_v2.genreId = :id AND timesPlayed >= ${DataConstants.MIN_MOST_PLAYED_TIMES}
        ORDER BY timesPlayed DESC
        LIMIT ${DataConstants.MAX_MOST_PLAYED_ITEMS}
    """)
    abstract fun observeMostPlayed(id: String): Flow<List<MediaStoreSongsView>>
    //
    @Query("""
        REPLACE INTO most_played_genre_v2(songId, genreId, timesPlayed) VALUES (
        :songId,
        :genreId,
        COALESCE((SELECT timesPlayed FROM most_played_genre_v2 WHERE songId = :songId AND genreId = :genreId), 0) + 1
    )
    """)
    abstract suspend fun insertMostPlayed(genreId: String, songId: String)

    @Query("""
        SELECT DISTINCT songs_view.artistId AS id, songs_view.artist AS name, count(*) AS songs, MIN(dateAdded) as dateAdded
        FROM mediastore_genre_track
            JOIN songs_view ON mediastore_genre_track.songId = songs_view.id
        WHERE mediastore_genre_track.genreId = :id
        GROUP BY artistId
        ORDER BY lower(name) COLLATE UNICODE ASC
    """)
    abstract fun observeRelatedArtists(id: String): Flow<List<MediaStoreArtistsView>>

    @Query("""
        SELECT songs_view.*
        FROM mediastore_genre_track
            JOIN songs_view ON mediastore_genre_track.songId = songs_view.id
        WHERE mediastore_genre_track.genreId = :id
            AND strftime('%s','now') - dateAdded < ${DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS}
        ORDER BY dateAdded DESC, lower(title) COLLATE UNICODE ASC
    """)
    abstract fun observeRecentlyAddedSongs(id: String): Flow<List<MediaStoreSongsView>>

}