package dev.olog.data.repository.track

import android.net.Uri
import dev.olog.core.comparator.SongComparators
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.MediaStoreUtils
import dev.olog.data.mediastore.audio.MediaStoreAudioDao
import dev.olog.data.mediastore.audio.toSong
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SongRepository @Inject constructor(
    private val dao: MediaStoreAudioDao,
    private val mediaStoreUtils: MediaStoreUtils,
    private val sortPreferences: SortPreferences,
) : SongGateway {

    override suspend fun getAll(): List<Song> {
        return observeAll().first()
    }

    override fun observeAll(): Flow<List<Song>> {
        val itemsFlow = dao.observeAll()
            .filterListItem { !it.isPodcast }
            .mapListItem { it.toSong() }
        val sortFlow = sortPreferences.observeAllTracksSort()
        return combine(itemsFlow, sortFlow) { items, sort ->
            items.sortedWith(SongComparators.invoke(sort))
        }
    }

    override suspend fun getByParam(id: Long): Song? {
        return observeByParam(id).first()
    }

    override fun observeByParam(id: Long): Flow<Song?> {
        return dao.observeById(id.toString())
            .map { it?.toSong() }
    }

    override suspend fun deleteSingle(id: Long) {
        mediaStoreUtils.deleteSingle(id) {
            getByParam(id)?.path
        }
    }

    override suspend fun deleteGroup(ids: List<Song>) {
        mediaStoreUtils.deleteGroup(ids) { id ->
            getByParam(id)?.path
        }
    }

    override suspend fun getByUri(uri: Uri): Song? {
        return mediaStoreUtils.getByUri(uri) { displayName ->
            dao.getByDisplayName(displayName)?.toSong()
        }
    }

    override suspend fun getByAlbumId(albumId: Long): Song? {
        return dao.getByAlbumId(albumId.toString())?.toSong()
    }
}