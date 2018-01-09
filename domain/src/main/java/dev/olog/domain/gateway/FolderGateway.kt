package dev.olog.domain.gateway

import dev.olog.domain.entity.Folder
import io.reactivex.Flowable

interface FolderGateway :
        BaseGateway<Folder, String>,
        ChildsHasSongs<String>,
        HasMostPlayed,
        HasCreatedImages {

    fun getAllUnfiltered(): Flowable<List<Folder>>

}