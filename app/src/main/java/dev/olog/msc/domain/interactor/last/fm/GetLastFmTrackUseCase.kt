package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.api.last.fm.model.SearchedTrack
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.LastFmCacheGateway
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetLastFmTrackUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: LastFmCacheGateway

) : SingleUseCaseWithParam<SearchedTrack, Long>(schedulers){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(songId: Long): Single<SearchedTrack> {
        return gateway.getTrack(songId)
    }

}