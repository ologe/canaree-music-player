package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.base.*

interface FolderGateway :
    BaseGateway<Folder, Long>,
    ChildHasTracks<Long>,
    HasMostPlayed,
    HasSiblings<Folder, Long>,
    HasRelatedArtists<Long>,
    HasRecentlyAddedSongs<Long> {

    fun getAllBlacklistedIncluded(): List<Folder>

}