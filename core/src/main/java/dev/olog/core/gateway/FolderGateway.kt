package dev.olog.core.gateway

import dev.olog.core.entity.track.Folder

interface FolderGateway :
    BaseGateway<Folder, Path>,
    ChildHasTracks<Path>,
    HasMostPlayed,
    HasSiblings<Folder, Path>,
    HasRelatedArtists<Path>,
    HasRecentlyAddedSongs<Path> {

    fun getAllBlacklistedIncluded(): List<Folder>

    /**
     * Hashcode = path.tohashCode()
     */
    fun getByHashCode(hashCode: Int): Folder?

}