package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Folder
import dev.olog.domain.gateway.base.*

interface FolderGateway :
    BaseGateway<Folder, String>,
    ChildHasTracks<String>,
    HasMostPlayed,
    HasSiblings<Folder, String>,
    HasRelatedArtists<String>,
    HasRecentlyAddedSongs<String> {

    fun getAllBlacklistedIncluded(): List<Folder>

}