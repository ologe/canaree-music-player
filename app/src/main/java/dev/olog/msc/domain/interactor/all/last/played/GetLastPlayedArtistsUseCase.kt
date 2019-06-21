package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.core.entity.Artist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetLastPlayedArtistsUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val artistGateway: ArtistGateway,
        private val appPreferencesUseCase: AppPreferencesGateway

): ObservableUseCase<List<Artist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Artist>> {
        return Observables.combineLatest(
                artistGateway.getLastPlayed(),
                appPreferencesUseCase.observeLibraryRecentPlayedVisibility()) { artists, show ->
            if (show){
                artists
            } else {
                listOf()
            }
        }
    }
}