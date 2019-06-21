package dev.olog.msc.domain.gateway

import dev.olog.core.entity.Folder
import io.reactivex.Observable

interface FolderGateway :
        BaseGateway<Folder, String>,
        ChildsHasSongs<String>,
        HasMostPlayed {

    fun getAllUnfiltered(): Observable<List<Folder>>

}