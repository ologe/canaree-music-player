package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetLastPlayedAlbumsUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val albumGateway: AlbumGateway,
        private val appPreferencesUseCase: AppPreferencesGateway

): ObservableUseCase<List<Album>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return Observables.combineLatest(
                albumGateway.getLastPlayed(),
                appPreferencesUseCase.observeLibraryRecentPlayedVisibility()) { albums, show ->
                    if (show){
                        albums
                    } else {
                        listOf()
                    }
                }
    }
}