package dev.olog.core.gateway

import dev.olog.core.entity.Folder
import dev.olog.core.entity.Song

interface FolderGateway2 :
    BaseGateway<Folder, Path>,
    ChildHasTracks2<Song, Path>