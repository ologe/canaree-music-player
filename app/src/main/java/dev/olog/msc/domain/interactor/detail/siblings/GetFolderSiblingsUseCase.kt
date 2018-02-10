package dev.olog.msc.domain.interactor.detail.siblings

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetFolderSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: FolderGateway

) : FlowableUseCaseWithParam<List<Folder>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId) : Flowable<List<Folder>> {
        val folderPath = mediaId.categoryValue

        return gateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.path != folderPath }
                        .toList()
                }
    }
}
