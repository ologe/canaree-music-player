package dev.olog.core.gateway

import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song

interface FolderGateway :
    BaseGateway<Folder, Path>,
    ChildHasTracks<Song, Path>,
    HasMostPlayed,
    HasSiblings<Folder, Path>,
    HasRelatedArtists<Path> {

    fun getAllBlacklistedIncluded(): List<Folder>

    /**
     * Hashcode = path.tohashCode()
     */
    fun getByHashCode(hashCode: Int): Folder?

}