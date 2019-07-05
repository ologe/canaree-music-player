package dev.olog.core.gateway.track

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id
import io.reactivex.Completable

interface SongGateway :
    BaseGateway<Song, Id> {

    fun deleteSingle(id: Id): Completable
    fun deleteGroup(ids: List<Song>): Completable

    fun getByUri(uri: Uri): Song?

    fun getByAlbumId(albumId: Id): Song?

}