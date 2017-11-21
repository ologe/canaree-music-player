package dev.olog.domain.gateway

import dev.olog.domain.entity.Artist

interface ArtistGateway :
        BaseGateway<Artist, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Artist>