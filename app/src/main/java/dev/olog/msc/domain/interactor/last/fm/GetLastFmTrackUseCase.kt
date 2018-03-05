package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetLastFmTrackUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val gateway: LastFmGateway

): SingleUseCaseWithParam<LastFmTrack, LastFmTrackRequest>(schedulers) {

    override fun buildUseCaseObservable(param: LastFmTrackRequest): Single<LastFmTrack> {
        val (id, title, artist) = param
        return gateway.getTrack(id, title, artist)
    }
}

data class LastFmTrackRequest(
        val id: Long,
        val title: String,
        val artist: String
)