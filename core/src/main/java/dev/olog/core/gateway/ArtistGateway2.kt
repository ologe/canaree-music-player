package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song

interface ArtistGateway2 :
    BaseGateway2<Artist, Id>,
    ChildHasTracks2<Song, Id>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist>