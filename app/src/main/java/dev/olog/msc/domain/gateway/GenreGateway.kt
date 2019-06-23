package dev.olog.msc.domain.gateway

import dev.olog.core.entity.track.Genre

interface GenreGateway  :
        BaseGateway<Genre, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed