package dev.olog.domain.gateway

import dev.olog.domain.entity.Folder

interface FolderGateway :
        BaseGateway<Folder, String>,
        ChildsHasSongs<String>,
        HasMostPlayed<String>