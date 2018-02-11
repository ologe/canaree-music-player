package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Folder
import io.reactivex.Completable
import io.reactivex.Observable

interface FolderGateway :
        BaseGateway<Folder, String>,
        ChildsHasSongs<String>,
        HasMostPlayed,
        HasCreatedImages {

    fun getAllUnfiltered(): Observable<List<Folder>>

    fun renameFolder(oldPath: String, newFolderName: String): Completable

}