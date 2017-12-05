package dev.olog.domain.interactor.search

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertSearchAlbumUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<Album>(scheduler) {

    override fun buildUseCaseObservable(param: Album): Completable {
        return recentSearchesGateway.insertAlbum(param)
    }
}