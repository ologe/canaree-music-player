package dev.olog.msc.domain.interactor.search.delete

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.RecentSearchesGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteRecentSearchFolderUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<Long>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(folderId: Long): Completable {
        return recentSearchesGateway.deleteFolder(folderId)
    }
}