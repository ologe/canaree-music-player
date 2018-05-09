package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetFolderUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: FolderGateway

) : ObservableUseCaseUseCaseWithParam<Folder, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Folder> {
        val folderPath = mediaId.categoryValue
        return gateway.getByParam(folderPath)
    }
}
