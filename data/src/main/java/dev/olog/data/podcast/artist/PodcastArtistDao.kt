package dev.olog.data.podcast.artist

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.DataConstants.MAX_LAST_PLAYED
import dev.olog.data.DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS
import dev.olog.data.mediastore.podcast.MediaStorePodcastsView
import dev.olog.data.mediastore.podcast.artist.MediaStorePodcastArtistsView
import dev.olog.data.mediastore.podcast.artist.MediaStorePodcastArtistsViewSorted
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_PODCAST_ARTISTS_EPISODES
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION
import dev.olog.data.sort.db.SORT_TYPE_TRACK_NUMBER
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
abstract class PodcastArtistDao {

    companion object {
        @Language("RoomSql")
        private const val EPISODES_QUERY = """
SELECT podcasts_view.*
FROM podcasts_view LEFT JOIN sort ON TRUE
WHERE artistId = :id AND sort.tableName = '${SORT_TABLE_PODCAST_ARTISTS_EPISODES}'
ORDER BY
-- album, then title
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND album = '${UNKNOWN_STRING}' THEN -1 END, -- when unknown move last
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(album) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(album) END COLLATE UNICODE DESC,
-- duration, then title
CASE WHEN sort.columnName = '${SORT_TYPE_DURATION}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN duration END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_DURATION}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN duration END DESC,
-- date, then title
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END DESC,
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END ASC,
-- disc number, track number, then title
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN discNumber END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN trackNumber END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN discNumber END DESC,
CASE WHEN sort.columnName = '${SORT_TYPE_TRACK_NUMBER}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN trackNumber END DESC,
-- also, CASE WHEN sort.columnName = 'title'
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(title) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(title) END COLLATE UNICODE DESC
"""
    }

    @Query("SELECT * from podcast_artists_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStorePodcastArtistsViewSorted>

    @Query("SELECT * from podcast_artists_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStorePodcastArtistsViewSorted>>

    @Query("SELECT * from podcast_artists_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStorePodcastArtistsView?

    @Query("SELECT * from podcast_artists_view WHERE id = :id")
    abstract fun observeById(id: String): Flow<MediaStorePodcastArtistsView?>

    @Query(EPISODES_QUERY)
    // todo made suspend
    abstract fun getTracksById(id: String): List<MediaStorePodcastsView>

    @Query(EPISODES_QUERY)
    abstract fun observeTracksById(id: String): Flow<List<MediaStorePodcastsView>>

    @Query("""
        SELECT podcast_artists_view.* 
        FROM podcast_artists_view JOIN last_played_podcast_artists ON podcast_artists_view.id = last_played_podcast_artists.id
        ORDER BY last_played_podcast_artists.dateAdded DESC
        LIMIT $MAX_LAST_PLAYED
    """)
    abstract fun observeLastPlayed(): Flow<List<MediaStorePodcastArtistsView>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLastPlayed(entity: LastPlayedPodcastArtistEntity)

    @Query("""
        SELECT *
        FROM podcast_artists_view
        WHERE strftime('%s','now') - dateAdded < $RECENTLY_ADDED_PERIOD_IN_SECONDS
        ORDER BY dateAdded DESC
    """)
    abstract fun observeRecentlyAdded(): Flow<List<MediaStorePodcastArtistsView>>

}