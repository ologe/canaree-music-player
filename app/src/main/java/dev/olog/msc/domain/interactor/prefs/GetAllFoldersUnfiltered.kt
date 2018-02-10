package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
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