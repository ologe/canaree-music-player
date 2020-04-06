package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.base.BaseGateway
import dev.olog.domain.gateway.base.ChildHasTracks
import dev.olog.domain.gateway.base.HasLastPlayed
import dev.olog.domain.gateway.base.HasRecentlyAdded

interface ArtistGateway :
    BaseGateway<Artist, Long>,
    ChildHasTracks<Long>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist>