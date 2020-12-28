package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.*

interface FolderGateway :
    BaseGateway<Folder, Path>,
    ChildHasTracks<Path, Song>,
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