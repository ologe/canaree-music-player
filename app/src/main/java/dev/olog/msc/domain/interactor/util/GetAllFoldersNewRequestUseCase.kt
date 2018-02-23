package dev.olog.msc.domain.interactor.util

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllFoldersNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: FolderGateway

) : ObservableUseCase<List<Folder>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Folder>> {
        return gateway.getAllNewRequest()
    }
}