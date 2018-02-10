package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Album
import io.reactivex.Flowable

interface AlbumGateway :
        BaseGateway<Album, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Album> {

    fun getAllAlbumsForUtils(): Flowable<List<Album>>

}
