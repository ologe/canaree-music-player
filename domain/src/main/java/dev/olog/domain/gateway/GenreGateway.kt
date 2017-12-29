package dev.olog.domain.gateway

import dev.olog.domain.entity.Genre

interface GenreGateway  :
        BaseGateway<Genre, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed,
        HasCreatedImages