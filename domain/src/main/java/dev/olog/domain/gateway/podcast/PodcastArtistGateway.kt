package dev.olog.domain.gateway.podcast

import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.base.*

interface PodcastArtistGateway :
    BaseGateway<Artist, Id>,
    HasLastPlayed<Artist>,
    HasRecentlyAdded<Artist>,
    ChildHasTracks<Id>,
    HasSiblings<Artist, Id>