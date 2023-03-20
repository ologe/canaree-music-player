package dev.olog.data.queries

import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.MediaStoreAudioView
import dev.olog.data.mediastore.MediaStoreAudioViewsDao
import dev.olog.data.mediastore.MediaStoreQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

internal class AudioQueries @Inject constructor(
    private val dao: MediaStoreAudioViewsDao,
    private val sortPrefs: SortPreferences,
    private val query: MediaStoreQuery,
) {

    fun getAll(isPodcast: Boolean): List<MediaStoreAudioView> {
        if (isPodcast) {
            return dao.getAll(SimpleSQLiteQuery(getPodcastQuery()))
        }
        val sort = sortPrefs.getAllTracksSort()
        return dao.getAll(SimpleSQLiteQuery(getSongQuery(sort)))
    }

    fun observeAll(isPodcast: Boolean): Flow<List<MediaStoreAudioView>> {
        if (isPodcast) {
            return dao.observeAll(SimpleSQLiteQuery(getPodcastQuery()))
        }
        return sortPrefs.observeAllTracksSort()
            .flatMapLatest { sort ->
                dao.observeAll(SimpleSQLiteQuery(getSongQuery(sort)))
            }
    }

    private fun getSongQuery(
        sort: SortEntity
    ): String {
        return """
            SELECT * FROM mediastore_audio
            WHERE ${AudioColumns.IS_PODCAST} = 0 
            ORDER BY ${songSortOrder(sort)}
        """
    }

    private fun getPodcastQuery(
        // TODO add sort support
    ): String {
        return """
            SELECT * FROM mediastore_audio
            WHERE ${AudioColumns.IS_PODCAST} <> 0 
            ORDER BY ${podcastSortOrder()}
        """
    }

    fun getById(id: Long): MediaStoreAudioView? {
        return dao.getById(id)
    }

    fun observeById(id: Long): Flow<MediaStoreAudioView?> {
        return dao.observeById(id)
    }

    fun getByAlbumId(id: Long): MediaStoreAudioView? {
        return dao.getByAlbumId(id)
    }

    // TODO ensure works as expected
    fun getByUri(uri: Uri): MediaStoreAudioView? {
        val displayName = query.getDisplayNameByUri(uri) ?: return null
        return dao.getByDisplayName(displayName)
    }

    private fun podcastSortOrder(): String {
        return AudioColumns.TITLE
    }

    private fun songSortOrder(sort: SortEntity): String {
        val direction = sort.arranging
        return when (sort.type) {
            SortType.TITLE -> "${AudioColumns.TITLE} $direction"
            SortType.ARTIST -> "CASE WHEN ${AudioColumns.ARTIST} = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, ${AudioColumns.ARTIST} $direction, ${AudioColumns.TITLE} $direction"
            SortType.ALBUM -> "CASE WHEN ${AudioColumns.ALBUM} = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, ${AudioColumns.ALBUM} $direction, ${AudioColumns.TITLE} $direction"
            SortType.ALBUM_ARTIST -> "${AudioColumns.ALBUM_ARTIST} $direction, ${AudioColumns.TITLE} $direction"
            SortType.DURATION -> "${AudioColumns.DURATION} $direction"
            SortType.RECENTLY_ADDED -> "${AudioColumns.DATE_ADDED} ${!direction}, ${AudioColumns.TITLE} $direction"
            SortType.TRACK_NUMBER,
            SortType.CUSTOM -> AudioColumns.TITLE
        }
    }

}