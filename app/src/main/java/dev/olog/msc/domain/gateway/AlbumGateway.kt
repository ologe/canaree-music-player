package dev.olog.msc.domain.gateway

import dev.olog.core.entity.Album
import io.reactivex.Observable

interface AlbumGateway :
        BaseGateway<Album, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Album> {

    fun observeByArtist(artistId: Long) : Observable<List<Album>>

}
