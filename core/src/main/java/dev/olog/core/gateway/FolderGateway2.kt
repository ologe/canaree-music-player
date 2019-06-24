package dev.olog.core.gateway

import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song

interface FolderGateway2 :
        BaseGateway2<Folder, Path>,
        ChildHasTracks2<Song, Path>,
        HasMostPlayed2,
        HasSiblings<Folder, Path> {

    fun getAllBlacklistedIncluded(): List<Folder>

}