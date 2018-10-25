package dev.olog.msc.domain.interactor.all.recently.added

import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.all.GetAllPodcastAlbumsUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetRecentlyAddedPodcastsAlbumsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getAllAlbumsUseCase: GetAllPodcastAlbumsUseCase,
        private val getAllPodcastsUseCase: GetAllPodcastUseCase,
        private val appPreferencesUseCase: AppPreferencesUseCase

) : ObservableUseCase<List<PodcastAlbum>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(): Observable<List<PodcastAlbum>> {
        return Observables.combineLatest(
                getRecentlyAddedPodcast(getAllPodcastsUseCase),
                getAllAlbumsUseCase.execute(),
                appPreferencesUseCase.observeLibraryNewVisibility()
        ) { songs, albums, show ->
            if (show){
                albums.filter { album -> songs.any { song -> song.albumId == album.id } }
            } else {
                listOf()
            }

        }
    }
}