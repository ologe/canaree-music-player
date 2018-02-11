package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetLastPlayedArtistsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway

): ObservableUseCase<List<Artist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Artist>> {
        return artistGateway.getLastPlayed()
    }
}