package dev.olog.domain.gateway

import dev.olog.domain.entity.Folder
import dev.olog.shared.MediaId
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