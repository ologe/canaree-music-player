package dev.olog.domain.interactor.search

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertSearchSongUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<Song>(scheduler) {

    override fun buildUseCaseObservable(param: Song): Completable {
        return recentSearchesGateway.insertSong(param)
    }
}