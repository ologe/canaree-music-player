package dev.olog.core.gateway

import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song

interface GenreGateway :
        BaseGateway<Genre, Id>,
        ChildHasTracks<Song, Id>,
        HasMostPlayed,
        HasSiblings<Genre, Id>,
        HasRelatedArtists<Id>