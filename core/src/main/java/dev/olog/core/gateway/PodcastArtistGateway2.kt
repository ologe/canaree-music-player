package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song

interface PodcastArtistGateway2 :
    BaseGateway2<Artist, Id>,
    HasLastPlayed<Artist>,
    HasRecentlyAdded<Artist>,
    ChildHasTracks2<Song, Id>