package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.base.*

interface ArtistGateway :
    BaseGateway<Artist, Id>,
    ChildHasTracks<Id>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist>