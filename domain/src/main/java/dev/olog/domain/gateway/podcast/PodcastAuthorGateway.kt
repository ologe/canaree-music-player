package dev.olog.domain.gateway.podcast

import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.base.*

interface PodcastAuthorGateway :
    BaseGateway<Artist, Long>,
    HasLastPlayed<Artist>,
    HasRecentlyAdded<Artist>,
    ChildHasTracks<Long>,
    HasSiblings<Artist, Long>