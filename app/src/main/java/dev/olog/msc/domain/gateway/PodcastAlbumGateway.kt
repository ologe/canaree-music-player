package dev.olog.msc.domain.gateway

import dev.olog.core.entity.PodcastAlbum
import io.reactivex.Observable

interface PodcastAlbumGateway :
        BaseGateway<PodcastAlbum, Long>,
        ChildsHasPodcasts<Long>,
        HasLastPlayed<PodcastAlbum> {

    fun observeByArtist(artistId: Long) : Observable<List<PodcastAlbum>>

}