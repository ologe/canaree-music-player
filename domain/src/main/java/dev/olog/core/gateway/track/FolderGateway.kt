package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.base.*

interface FolderGateway :
    BaseGateway<Folder, String>,
    ChildHasTracks<String>,
    HasMostPlayed,
    HasSiblings<Folder, String>,
    HasRelatedArtists<String>,
    HasRecentlyAddedSongs<String> {

    fun getAllBlacklistedIncluded(): List<Folder>

}