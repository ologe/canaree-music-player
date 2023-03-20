package dev.olog.core.gateway.track

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import kotlinx.coroutines.flow.Flow

interface SongGateway {

    fun getAll(): List<Song>
    fun observeAll(): Flow<List<Song>>

    fun getByParam(id: Long): Song?
    fun observeByParam(id: Long): Flow<Song?>

    fun getByUri(uri: Uri): Song?

    @Deprecated(message = "remove")
    fun getByAlbumId(albumId: Id): Song?

}