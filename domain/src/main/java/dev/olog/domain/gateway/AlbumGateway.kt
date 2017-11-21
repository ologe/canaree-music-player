package dev.olog.domain.gateway

import dev.olog.domain.entity.Album

interface AlbumGateway :
        BaseGateway<Album, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Album>
