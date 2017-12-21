package dev.olog.domain.gateway

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import io.reactivex.Flowable

interface ArtistGateway :
        BaseGateway<Artist, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Artist>,
        HasCreatedImages {

    fun getAlbums(artistId: Long): Flowable<List<Album>>

}