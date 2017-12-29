package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Folder
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
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
