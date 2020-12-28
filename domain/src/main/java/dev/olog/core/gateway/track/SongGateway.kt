package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id
import java.net.URI

interface SongGateway :
    BaseGateway<Track, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Track>)

    suspend fun getByUri(uri: URI): Track?

    suspend fun getByAlbumId(albumId: Id): Track?

}