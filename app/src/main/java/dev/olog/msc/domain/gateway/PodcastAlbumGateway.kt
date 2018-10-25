package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.PodcastAlbum
import io.reactivex.Observable

interface PodcastAlbumGateway :
        BaseGateway<PodcastAlbum, Long>,
        ChildsHasPodcasts<Long>,
        HasLastPlayed<PodcastAlbum> {

    fun observeByArtist(artistId: Long) : Observable<List<PodcastAlbum>>

}