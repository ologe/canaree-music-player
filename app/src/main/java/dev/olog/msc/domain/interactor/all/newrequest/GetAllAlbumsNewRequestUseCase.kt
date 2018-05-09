package dev.olog.msc.domain.interactor.all.newrequest

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllAlbumsNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: AlbumGateway

) : ObservableUseCase<List<Album>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return gateway.getAllNewRequest()
    }
}