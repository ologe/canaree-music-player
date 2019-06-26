package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song

interface PodcastArtistGateway :
        BaseGateway<Artist, Id>,
        HasLastPlayed<Artist>,
        HasRecentlyAdded<Artist>,
        ChildHasTracks<Song, Id>,
        HasSiblings<Artist, Id>