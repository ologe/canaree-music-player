package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.*

interface ArtistGateway :
    BaseGateway<Artist, Id>,
    ChildHasTracks<Id, Song>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist>