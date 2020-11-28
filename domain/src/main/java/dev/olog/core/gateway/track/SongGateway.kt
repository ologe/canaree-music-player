package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id
import java.net.URI

interface SongGateway :
    BaseGateway<Song, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Song>)

    fun getByUri(uri: URI): Song?

    fun getByAlbumId(albumId: Id): Song?

}