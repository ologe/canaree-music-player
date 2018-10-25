package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.PodcastArtist


interface PodcastArtistGateway :
        BaseGateway<PodcastArtist, Long>,
        ChildsHasPodcasts<Long>,
        HasLastPlayed<PodcastArtist>