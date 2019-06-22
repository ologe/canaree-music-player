package dev.olog.core.gateway

import dev.olog.core.entity.Artist
import dev.olog.core.entity.Song

interface ArtistGateway2 :
    BaseGateway<Artist, Id>,
    ChildHasTracks2<Song, Id>