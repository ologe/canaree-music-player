package dev.olog.core.gateway.track

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id

interface SongGateway :
    BaseGateway<Song, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Song>)

    fun getByUri(uri: Uri): Song?

    fun getByAlbumId(albumId: Id): Song?

}