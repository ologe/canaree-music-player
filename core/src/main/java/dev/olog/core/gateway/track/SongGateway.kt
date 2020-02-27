package dev.olog.core.gateway.track

import dev.olog.core.entity.PureUri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id

interface SongGateway :
    BaseGateway<Song, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Id>)

    fun getByUri(uri: PureUri): Song?

    fun getByAlbumId(albumId: Id): Song?

}