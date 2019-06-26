package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song

interface ArtistGateway :
        BaseGateway<Artist, Id>,
        ChildHasTracks<Song, Id>,
        HasRecentlyAdded<Artist>,
        HasLastPlayed<Artist>,
        HasSiblings<Artist, Id>