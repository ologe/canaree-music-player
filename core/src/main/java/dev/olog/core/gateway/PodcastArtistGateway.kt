package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist

interface PodcastArtistGateway :
    BaseGateway<Artist, Id>,
    HasLastPlayed<Artist>,
    HasRecentlyAdded<Artist>,
    ChildHasTracks<Id>,
    HasSiblings<Artist, Id>