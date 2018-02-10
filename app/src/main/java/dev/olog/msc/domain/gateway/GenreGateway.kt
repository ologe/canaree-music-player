package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Genre

interface GenreGateway  :
        BaseGateway<Genre, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed,
        HasCreatedImages