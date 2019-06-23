package dev.olog.core.gateway

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song

interface AlbumGateway2 :
    BaseGateway2<Album, Id>,
    ChildHasTracks2<Song, Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>