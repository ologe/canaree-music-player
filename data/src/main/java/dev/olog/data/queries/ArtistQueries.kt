package dev.olog.data.queries

import android.provider.MediaStore
import android.provider.MediaStore.Audio.*
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.artist.MediaStoreArtistDao
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ArtistQueries @Inject constructor(
    private val dao: MediaStoreArtistDao,
    private val sortPrefs: SortPreferences,
) {

    fun getAll(isPodcast: Boolean): List<MediaStoreArtistEntity> {
        if (isPodcast) {
            return dao.getAll(SimpleSQLiteQuery(getPodcastQuery()))
        }
        val sort = sortPrefs.getAllArtistsSort()
        return dao.getAll(SimpleSQLiteQuery(getSongQuery(sort)))
    }

    fun observeAll(isPodcast: Boolean): Flow<List<MediaStoreArtistEntity>> {
        if (isPodcast) {
            return dao.observeAll(SimpleSQLiteQuery(getPodcastQuery()))
        }
        return sortPrefs.observeAllArtistsSort()
            .flatMapLatest { sort ->
                dao.observeAll(SimpleSQLiteQuery(getSongQuery(sort)))
            }
    }

    fun getById(id: Long): MediaStoreArtistEntity? {
        return dao.getById(id)
    }

    fun observeById(id: Long): Flow<MediaStoreArtistEntity?> {
        return dao.observeById(id)
    }

    fun getSongList(isPodcast: Boolean, id: Long): List<MediaStoreAudioEntity> {
        val sort = if (isPodcast) {
            // TODO add sort support
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        } else {
            sortPrefs.getDetailArtistSort()
        }
        return dao.getTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
    }

    fun observeSongList(isPodcast: Boolean, id: Long): Flow<List<MediaStoreAudioEntity>> {
        if (isPodcast) {
            // TODO add sort support
            val sort = SortEntity(SortType.TITLE, SortArranging.ASCENDING)
            return dao.observeTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
        }

        return sortPrefs.observeDetailArtistSort()
            .flatMapLatest { sort ->
                dao.observeTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
            }
    }

    private fun getSongQuery(
        sort: SortEntity
    ): String {
        return """
            SELECT * FROM mediastore_artists
            WHERE ${AudioColumns.IS_PODCAST} = 0
            ORDER BY ${songSortOrder(sort)}
        """
    }

    private fun getPodcastQuery(
        // TODO add sort support
    ): String {
        return """
            SELECT * FROM mediastore_artists
            WHERE ${AudioColumns.IS_PODCAST} <> 0
            ORDER BY ${podcastSortOrder()}
        """
    }

    private fun getSongListQuery(
        sort: SortEntity
    ): String {
        return """
            SELECT * FROM mediastore_audio
            WHERE ${AudioColumns.ARTIST_ID} = ?
            ORDER BY ${QueryUtils.songListSortOrder(sort, AudioColumns.TITLE)}
        """
    }

    fun observeRecentlyAdded(isPodcast: Boolean): Flow<List<MediaStoreArtistEntity>> {
        return dao.observeRecentlyAdded(if (isPodcast) 1 else 0)
    }

    private fun podcastSortOrder(): String {
        return AudioColumns.ARTIST
    }

    private fun songSortOrder(sort: SortEntity): String {
        val direction = sort.arranging
        return when (sort.type) {
            SortType.ARTIST -> "CASE WHEN ${AudioColumns.ARTIST} = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, ${AudioColumns.ARTIST} $direction"
            SortType.ALBUM_ARTIST -> "CASE WHEN ${AudioColumns.ALBUM_ARTIST} = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, ${AudioColumns.ALBUM_ARTIST} $direction"
            else -> AudioColumns.ARTIST
        }
    }

}