package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Genre
import dev.olog.core.gateway.base.*

interface GenreGateway :
    BaseGateway<Genre, Id>,
    ChildHasTracks<Id>,
    HasMostPlayed,
    HasSiblings<Genre, Id>,
    HasRelatedArtists<Id>,
    HasRecentlyAddedSongs<Id>