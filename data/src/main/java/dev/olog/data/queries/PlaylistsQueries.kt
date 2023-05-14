package dev.olog.data.queries

import android.provider.MediaStore.Audio.*
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.QueryMode
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDao
import dev.olog.data.mediastore.playlist.MediaStorePlaylistEntity
import dev.olog.shared.filterListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class PlaylistsQueries @Inject constructor(
    private val dao: MediaStorePlaylistDao,
    private val sortPrefs: SortPreferences,
) {

    fun getAll(mode: QueryMode): List<MediaStorePlaylistEntity> {
        return dao.getAll()
            .filter {
                when (mode) {
                    QueryMode.All -> true
                    QueryMode.Songs -> it.isPodcast == 0
                    QueryMode.Podcasts -> it.isPodcast == 1
                }
            }
    }

    fun observeAll(mode: QueryMode): Flow<List<MediaStorePlaylistEntity>> {
        return dao.observeAll()
            .filterListItem {
                when (mode) {
                    QueryMode.All -> true
                    QueryMode.Songs -> it.isPodcast == 0
                    QueryMode.Podcasts -> it.isPodcast == 1
                }
            }
    }

    fun getById(id: Long): MediaStorePlaylistEntity? {
        return dao.getById(id)
    }

    fun observeById(id: Long): Flow<MediaStorePlaylistEntity?> {
        return dao.observeById(id)
    }

    fun getSongList(isPodcast: Boolean, id: Long): List<MediaStoreAudioEntity> {
        if (isPodcast) {
            // TODO add sort support
            val sort = SortEntity(SortType.CUSTOM, SortArranging.ASCENDING)
            return dao.getTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
        }
        val sort = sortPrefs.getDetailPlaylistSort()
        return dao.getTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
    }

    fun observeSongList(isPodcast: Boolean, id: Long): Flow<List<MediaStoreAudioEntity>> {
        if (isPodcast) {
            // TODO add sort support
            val sort = SortEntity(SortType.CUSTOM, SortArranging.ASCENDING)
            return dao.observeTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
        }
        return sortPrefs.observeDetailPlaylistSort()
            .flatMapLatest { sort ->
                dao.observeTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
            }
    }

    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistEntity>> {
        return dao.observeRelatedArtists(id)
    }

    private fun getSongListQuery(
        sort: SortEntity
    ): String {
        //language=RoomSql
        return """
            SELECT mediastore_audio.*
            FROM mediastore_playlist_members_internal members 
                JOIN mediastore_audio ON members.audio_id = mediastore_audio._id
            WHERE members.playlist_id = :id
            ORDER BY ${QueryUtils.songListSortOrder(sort, Playlists.Members.PLAY_ORDER)}
        """
    }

}