package dev.olog.core.gateway

import dev.olog.core.entity.Genre
import dev.olog.core.entity.Song

interface GenreGateway2 :
    BaseGateway<Genre, Id>,
    ChildHasTracks2<Song, Id>