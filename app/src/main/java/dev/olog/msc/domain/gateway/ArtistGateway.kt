package dev.olog.msc.domain.gateway

import dev.olog.core.entity.Artist

interface ArtistGateway :
        BaseGateway<Artist, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Artist>