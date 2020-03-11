package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.base.*

interface FolderGateway :
    BaseGateway<Folder, Id>,
    ChildHasTracks<Id>,
    HasMostPlayed,
    HasSiblings<Folder, Id>,
    HasRelatedArtists<Id>,
    HasRecentlyAddedSongs<Id> {

    fun getAllBlacklistedIncluded(): List<Folder>

}