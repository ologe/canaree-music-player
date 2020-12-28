package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.*

interface PodcastArtistGateway :
    BaseGateway<Artist, Id>,
    HasLastPlayed<Artist>,
    HasRecentlyAdded<Artist>,
    ChildHasTracks<Id, Song>,
    HasSiblings<Artist, Id>