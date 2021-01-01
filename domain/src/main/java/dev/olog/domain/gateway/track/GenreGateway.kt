package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Genre
import dev.olog.domain.gateway.base.*

interface GenreGateway :
    BaseGateway<Genre, Id>,
    ChildHasTracks<Id>,
    HasMostPlayed,
    HasSiblings<Genre, Id>,
    HasRelatedArtists<Id>,
    HasRecentlyAddedSongs<Id>