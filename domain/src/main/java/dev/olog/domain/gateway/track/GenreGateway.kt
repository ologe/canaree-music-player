package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Genre
import dev.olog.domain.gateway.base.*

interface GenreGateway :
    BaseGateway<Genre, Long>,
    ChildHasTracks<Long>,
    HasMostPlayed,
    HasSiblings<Genre, Long>,
    HasRelatedArtists<Long>,
    HasRecentlyAddedSongs<Long>