package dev.olog.data.song.folder

import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Query
import dev.olog.data.DataConstants.MAX_MOST_PLAYED_ITEMS
import dev.olog.data.DataConstants.MIN_MOST_PLAYED_TIMES
import dev.olog.data.DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsView
import dev.olog.data.mediastore.song.folder.MediaStoreFoldersView
import dev.olog.data.mediastore.song.folder.MediaStoreFoldersViewSorted
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_FOLDERS_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ALBUM_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION
import dev.olog.data.sort.db.SORT_TYPE_TRACK_NUMBER
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
abstract class FolderDao {

    companion object {
        @Language("RoomSql")
        private const val SONGS_QUERY = """
SELECT songs_view.*
FROM songs_view LEFT JOIN sort ON TRUE
WHERE directory = :directory AND sort.tableName = '$SORT_TABLE_FOLDERS_SONGS'
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

    @Query("SELECT * from folders_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStoreFoldersViewSorted>

    @Query("SELECT * from folders_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStoreFoldersViewSorted>>

    @Query("SELECT * from folders_view WHERE path = :directory")
    // todo made suspend
    abstract fun getByDirectory(directory: String): MediaStoreFoldersView?

    @Query("SELECT * from folders_view WHERE path = :directory")
    abstract fun observeByDirectory(directory: String): Flow<MediaStoreFoldersView?>

    @Query(SONGS_QUERY)
    // todo made suspend
    abstract fun getTracksByDirectory(directory: String): List<MediaStoreSongsView>

    @Query(SONGS_QUERY)
    abstract fun observeTracksByDirectory(directory: String): Flow<List<MediaStoreSongsView>>

    @Query("""
        SELECT DISTINCT directory as path, directoryName AS name, count(*) AS songs, MIN(dateAdded) as dateAdded
        FROM mediastore_audio
        WHERE isPodcast = false
        GROUP BY directory
        ORDER BY lower(name) COLLATE UNICODE ASC
    """)
    abstract fun getAllBlacklistedIncluded(): List<MediaStoreFoldersViewSorted>

    @Query("""
        SELECT songs_view.*
        FROM songs_view JOIN most_played_folder_v2 ON songs_view.id = most_played_folder_v2.songId
        WHERE most_played_folder_v2.path = :directory AND timesPlayed >= $MIN_MOST_PLAYED_TIMES
        ORDER BY timesPlayed DESC
        LIMIT $MAX_MOST_PLAYED_ITEMS
    """)
    abstract fun observeMostPlayed(directory: String): Flow<List<MediaStoreSongsView>>
//
    @Query("""
        REPLACE INTO most_played_folder_v2(songId, path, timesPlayed) VALUES (
        :songId,
        :directory,
        COALESCE((SELECT timesPlayed FROM most_played_folder_v2 WHERE songId = :songId AND path = :directory), 0) + 1
    )
    """)
    abstract suspend fun insertMostPlayed(directory: String, songId: String)

    @Query("""
        SELECT * 
        FROM folders_view
        WHERE path != :directory
        ORDER BY lower(name) COLLATE UNICODE ASC
    """)
    abstract fun observeSiblings(directory: String): Flow<List<MediaStoreFoldersView>>

    @Query("""
        SELECT DISTINCT artistId AS id, artist AS name, count(*) AS songs, MIN(dateAdded) as dateAdded
        FROM songs_view 
        WHERE directory = :directory
        GROUP BY artistId
        ORDER BY lower(name) COLLATE UNICODE ASC
    """)
    abstract fun observeRelatedArtists(directory: String): Flow<List<MediaStoreArtistsView>>

    @Query("""
        SELECT *
        FROM songs_view
        WHERE strftime('%s','now') - dateAdded < $RECENTLY_ADDED_PERIOD_IN_SECONDS AND directory = :directory
        ORDER BY dateAdded DESC, lower(title) COLLATE UNICODE ASC
    """)
    abstract fun observeRecentlyAddedSongs(directory: String): Flow<List<MediaStoreSongsView>>

    @Query("""
        SELECT *
        FROM folders_view
        WHERE path != :directory -- filter out same directory 
            AND path LIKE :directory || '/%' -- filter in sub-directory 
            AND path NOT LIKE :directory || '/%/%' -- filter out sub-sub-directories
        ORDER BY lower(name) COLLATE UNICODE ASC
    """)
    abstract suspend fun getDirectorySubFolders(directory: String): List<MediaStoreFoldersView>

    @Query("""
        SELECT songs_view.*
        FROM songs_view
        WHERE directory = :directory
        ORDER BY lower(title) COLLATE UNICODE ASC
    """)
    abstract suspend fun getDirectorySongs(directory: String): List<MediaStoreSongsView>

}
