package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Folder
import io.reactivex.Completable
import io.reactivex.Flowable

interface FolderGateway :
        BaseGateway<Folder, String>,
        ChildsHasSongs<String>,
        HasMostPlayed,
        HasCreatedImages {

    fun getAllUnfiltered(): Flowable<List<Folder>>

    fun renameFolder(oldPath: String, newFolderName: String): Completable

}