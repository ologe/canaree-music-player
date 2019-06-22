package dev.olog.msc.domain.interactor.all.recently.added

import dev.olog.core.entity.Artist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import dev.olog.msc.domain.interactor.all.GetAllArtistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetRecentlyAddedArtistsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val appPreferencesUseCase: PresentationPreferences

) : ObservableUseCase<List<Artist>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(): Observable<List<Artist>> {
        return Observables.combineLatest(
                getRecentlyAddedSong(getAllSongsUseCase),
                getAllArtistsUseCase.execute(),
                appPreferencesUseCase.observeLibraryNewVisibility()
        ) { songs, artists, show ->
            if (show){
                artists.filter { artist -> songs.any { song -> song.artistId == artist.id } }
            } else {
                listOf()
            }
        }
    }
}