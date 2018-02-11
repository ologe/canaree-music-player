package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Artist

interface ArtistGateway :
        BaseGateway<Artist, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Artist>,
        HasCreatedImages