package dev.olog.data.repository.track

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.queries.AudioQueries
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val queries: AudioQueries,
) : SongGateway {

    override fun getAll(): List<Song> {
        return queries.getAll(false).map { it.toSong() }
    }

    override fun observeAll(): Flow<List<Song>> {
        return queries.observeAll(false)
            .mapListItem { it.toSong() }
    }

    override fun getById(id: Long): Song? {
        return queries.getById(id)?.toSong()
    }

    override fun observeById(id: Long): Flow<Song?> {
        return queries.observeById(id).map { it?.toSong() }
    }

    override fun getByUri(uri: Uri): Song? {
        return queries.getByUri(uri)?.toSong()
    }

    override fun getByAlbumId(albumId: Long): Song? {
        return queries.getByAlbumId(albumId)?.toSong()
    }
}