package dev.olog.core.gateway

import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song

interface GenreGateway2 :
    BaseGateway2<Genre, Id>,
    ChildHasTracks2<Song, Id>