package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist

interface ArtistGateway :
    BaseGateway<Artist, Id>,
    ChildHasTracks<Id>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist>,
    HasSiblings<Artist, Id>