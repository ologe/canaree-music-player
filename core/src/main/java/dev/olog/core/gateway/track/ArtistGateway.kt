package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.ChildHasTracks
import dev.olog.core.gateway.base.HasLastPlayed
import dev.olog.core.gateway.base.HasRecentlyAdded

interface ArtistGateway :
    BaseGateway<Artist, Long>,
    ChildHasTracks<Long>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist>