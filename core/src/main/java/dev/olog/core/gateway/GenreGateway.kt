package dev.olog.core.gateway

import dev.olog.core.entity.track.Genre

interface GenreGateway :
    BaseGateway<Genre, Id>,
    ChildHasTracks<Id>,
    HasMostPlayed,
    HasSiblings<Genre, Id>,
    HasRelatedArtists<Id>,
    HasRecentlyAddedSongs<Id>