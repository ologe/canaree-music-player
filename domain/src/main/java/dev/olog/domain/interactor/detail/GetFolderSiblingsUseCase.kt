package dev.olog.domain.interactor.detail

import dev.olog.domain.entity.Folder
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import javax.inject.Inject

class GetFolderSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: FolderGateway

) : FlowableUseCase<List<Folder>>(schedulers) {


    override fun buildUseCaseObservable() = gateway.getAll()
}
