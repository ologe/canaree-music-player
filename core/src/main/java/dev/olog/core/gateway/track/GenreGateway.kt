package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Genre
import dev.olog.core.gateway.base.*

interface GenreGateway :
    BaseGateway<Genre, Long>,
    ChildHasTracks<Long>,
    HasMostPlayed,
    HasSiblings<Genre, Long>,
    HasRelatedArtists<Long>,
    HasRecentlyAddedSongs<Long>