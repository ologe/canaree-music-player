package dev.olog.domain.interactor.search

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteRecentSearchAlbumUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<Long>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(albumId: Long): Completable {
        return recentSearchesGateway.deleteAlbum(albumId)
    }
}