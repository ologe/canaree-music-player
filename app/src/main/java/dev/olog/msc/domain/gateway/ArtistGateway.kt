package dev.olog.msc.domain.gateway

import dev.olog.core.entity.track.Artist

interface ArtistGateway :
        BaseGateway<Artist, Long>,
        ChildsHasSongs<Long>