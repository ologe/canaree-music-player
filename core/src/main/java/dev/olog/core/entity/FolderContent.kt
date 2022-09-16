package dev.olog.core.entity

import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song

data class FolderContent(
    val subFolders: List<Folder>,
    val songs: List<Song>,
)