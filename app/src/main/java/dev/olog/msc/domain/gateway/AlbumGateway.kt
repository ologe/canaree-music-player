package dev.olog.msc.domain.gateway

import dev.olog.core.entity.track.Album
import io.reactivex.Observable

interface AlbumGateway :
        BaseGateway<Album, Long>,
        ChildsHasSongs<Long> {

    fun observeByArtist(artistId: Long) : Observable<List<Album>>

}
