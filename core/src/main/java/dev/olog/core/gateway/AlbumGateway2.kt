package dev.olog.core.gateway

import dev.olog.core.entity.Album
import dev.olog.core.entity.Song

interface AlbumGateway2 :
    BaseGateway<Album, Id>,
    ChildHasTracks2<Song, Id>