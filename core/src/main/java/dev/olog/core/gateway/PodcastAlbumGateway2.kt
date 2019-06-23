package dev.olog.core.gateway

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song

interface PodcastAlbumGateway2 :
    BaseGateway2<Album, Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    ChildHasTracks2<Song, Id>