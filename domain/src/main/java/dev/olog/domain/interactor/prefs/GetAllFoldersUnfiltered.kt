package dev.olog.domain.interactor.prefs

import dev.olog.domain.entity.Folder
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetAllFoldersUnfiltered @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: FolderGateway

) : FlowableUseCase<List<Folder>>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<List<Folder>> {
        return gateway.getAllUnfiltered()
    }
}