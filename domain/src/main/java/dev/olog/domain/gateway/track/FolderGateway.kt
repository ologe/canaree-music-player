package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Folder
import dev.olog.domain.gateway.base.*

interface FolderGateway :
    BaseGateway<Folder, Path>,
    ChildHasTracks<Path>,
    HasMostPlayed,
    HasSiblings<Folder, Path>,
    HasRelatedArtists<Path>,
    HasRecentlyAddedSongs<Path> {

    suspend fun getAllBlacklistedIncluded(): List<Folder>

    /**
     * Hashcode = path.tohashCode()
     */
    suspend fun getByHashCode(hashCode: Int): Folder?

}