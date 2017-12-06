package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Artist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetLastPlayedArtistsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway

): FlowableUseCase<List<Artist>>(schedulers) {

    override fun buildUseCaseObservable(): Flowable<List<Artist>> {
        return artistGateway.getLastPlayed()
    }
}