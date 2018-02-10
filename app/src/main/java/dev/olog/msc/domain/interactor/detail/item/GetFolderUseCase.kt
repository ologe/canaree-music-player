package dev.olog.msc.domain.interactor.detail.item

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetFolderUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: FolderGateway

) : FlowableUseCaseWithParam<Folder, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<Folder> {
        val folderPath = mediaId.categoryValue

        return gateway.getByParam(folderPath)
    }
}
