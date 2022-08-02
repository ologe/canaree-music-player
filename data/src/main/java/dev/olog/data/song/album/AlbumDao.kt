package dev.olog.data.song.album

import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.DataConstants.MAX_LAST_PLAYED
import dev.olog.data.DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.album.MediaStoreAlbumsView
import dev.olog.data.mediastore.song.album.MediaStoreAlbumsViewSorted
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_ALBUMS_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION
import dev.olog.data.sort.db.SORT_TYPE_TRACK_NUMBER
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
abstract class AlbumDao {

    companion object {
        @Language("RoomSql")
        private const val SONGS_QUERY = """
SELECT songs_view.*
FROM songs_view
    LEFT JOIN sort ON TRUE
WHERE albumId = :id AND sort.tableName = '$SORT_TABLE_ALBUMS_SONGS'
ORDER BY
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
-- default, and second sort
CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(title) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(title) END COLLATE UNICODE DESC
"""
    }

    @Query("SELECT * from albums_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStoreAlbumsViewSorted>

    @Query("SELECT * from albums_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStoreAlbumsViewSorted>>

    @Query("SELECT * from albums_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStoreAlbumsView?

    @Query("SELECT * from albums_view WHERE id = :id")
    abstract fun observeById(id: String): Flow<MediaStoreAlbumsView?>

    @Query(SONGS_QUERY)
    // todo made suspend
    abstract fun getTracksById(id: String): List<MediaStoreSongsView>

    @Query(SONGS_QUERY)
    abstract fun observeTracksById(id: String): Flow<List<MediaStoreSongsView>>

    @Query("""
        SELECT albums_view.* 
        FROM albums_view JOIN last_played_albums ON albums_view.id = last_played_albums.id
        ORDER BY last_played_albums.dateAdded DESC
        LIMIT $MAX_LAST_PLAYED
    """)
    abstract fun observeLastPlayed(): Flow<List<MediaStoreAlbumsView>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLastPlayed(entity: LastPlayedAlbumEntity)

    @Query("""
        SELECT *
        FROM albums_view
        WHERE strftime('%s','now') - dateAdded < $RECENTLY_ADDED_PERIOD_IN_SECONDS
        ORDER BY dateAdded DESC
    """)
    abstract fun observeRecentlyAdded(): Flow<List<MediaStoreAlbumsView>>

    @Query("""
        SELECT *
        FROM albums_view
        WHERE id != :id AND artistId IN (
            SELECT artistId
            FROM albums_view
            WHERE id = :id
        )
        ORDER BY lower(title) COLLATE UNICODE ASC
    """)
    abstract fun observeSiblings(id: String): Flow<List<MediaStoreAlbumsView>>

    @Query("""
        SELECT *
        FROM albums_view
        WHERE artistId = :artistId
        ORDER BY lower(title) COLLATE UNICODE ASC
    """)
    abstract fun observeArtistAlbums(artistId: String): Flow<List<MediaStoreAlbumsView>>

}