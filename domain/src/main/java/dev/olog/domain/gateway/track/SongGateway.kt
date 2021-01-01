package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.base.BaseGateway
import dev.olog.domain.gateway.base.Id
import java.net.URI

interface SongGateway :
    BaseGateway<Track, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Track>)

    suspend fun getByUri(uri: URI): Track?

    suspend fun getByAlbumId(albumId: Id): Track?

}