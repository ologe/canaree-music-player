package dev.olog.domain.gateway

import dev.olog.domain.entity.Playlist

interface PlaylistGateway :
        BaseGateway<Playlist, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed<String>