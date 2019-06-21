package dev.olog.msc.domain.gateway

import dev.olog.core.entity.PodcastArtist


interface PodcastArtistGateway :
        BaseGateway<PodcastArtist, Long>,
        ChildsHasPodcasts<Long>,
        HasLastPlayed<PodcastArtist>