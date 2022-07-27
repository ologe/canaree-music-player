package dev.olog.data.song.artist

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.DataConstants.MAX_LAST_PLAYED
import dev.olog.data.DataConstants.RECENTLY_ADDED_PERIOD
import dev.olog.data.db.last.played.LastPlayedArtistEntity
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsView
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsViewSorted
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_ARTISTS_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ALBUM_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION
import dev.olog.data.sort.db.SORT_TYPE_TRACK_NUMBER
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
abstract class ArtistDao {

    companion object {
        @Language("RoomSql")
        private const val SONGS_QUERY = """
SELECT songs_view.*
FROM songs_view
    LEFT JOIN sort ON TRUE
WHERE artistId = :id AND sort.tableName = '${SORT_TABLE_ARTISTS_SONGS}'
ORDER BY
-- album, then title
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND album = '${UNKNOWN_STRING}' THEN -1 END, -- when unknown move last
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(album) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(album) END COLLATE UNICODE DESC,
-- album artist, then title
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM_ARTIST}' AND albumArtist = '${UNKNOWN_STRING}' THEN -1 END, -- when unknown move last
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM_ARTIST}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(albumArtist) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM_ARTIST}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(albumArtist) END COLLATE UNICODE DESC,
-- duration, then title
CASE WHEN sort.columnName = '${SORT_TYPE_DURATION}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN duration END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_DURATION}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN duration END DESC,
-- date added, then title
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END DESC,
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END ASC,
-- disc number, track number, then title
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN discNumber END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN trackNumber END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN discNumber END DESC,
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN trackNumber END DESC,

-- default, and second sort
-- also, CASE WHEN sort.columnName = 'title'
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(title) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(title) END COLLATE UNICODE DESC;
"""
    }

    @Query("SELECT * from artists_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStoreArtistsViewSorted>

    @Query("SELECT * from artists_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStoreArtistsViewSorted>>

    @Query("SELECT * from artists_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStoreArtistsView?

    @Query("SELECT * from artists_view WHERE id = :id")
    abstract fun observeById(id: String): Flow<MediaStoreArtistsView?>

    @Query(SONGS_QUERY)
    // todo made suspend
    abstract fun getTracksById(id: String): List<MediaStoreSongsView>

    @Query(SONGS_QUERY)
    abstract fun observeTracksById(id: String): Flow<List<MediaStoreSongsView>>

    @Query("""
        SELECT artists_view.* 
        FROM artists_view JOIN last_played_artists ON artists_view.id = last_played_artists.id
        ORDER BY last_played_artists.dateAdded DESC
        LIMIT $MAX_LAST_PLAYED
    """)
    abstract fun observeLastPlayed(): Flow<List<MediaStoreArtistsView>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLastPlayed(entity: LastPlayedArtistEntity)

    @Query("""
        SELECT *
        FROM artists_view
        WHERE strftime('%s','now') - dateAdded < $RECENTLY_ADDED_PERIOD
        ORDER BY dateAdded DESC
    """)
    abstract fun observeRecentlyAdded(): Flow<List<MediaStoreArtistsView>>

}