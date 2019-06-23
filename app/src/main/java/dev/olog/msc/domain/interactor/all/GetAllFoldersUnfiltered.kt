package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Folder
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.FolderGateway2
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllFoldersUnfiltered @Inject constructor(
    scheduler: IoScheduler,
    private val gateway: FolderGateway2

) : ObservableUseCase<List<Folder>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<Folder>> {
        return Observable.just(gateway.getAllBlacklistedIncluded())
    }
}