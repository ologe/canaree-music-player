package dev.olog.msc.domain.interactor.all.recently.added

import dev.olog.core.entity.podcast.PodcastAlbum
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import dev.olog.msc.domain.interactor.all.GetAllPodcastAlbumsUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetRecentlyAddedPodcastsAlbumsUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val getAllAlbumsUseCase: GetAllPodcastAlbumsUseCase,
    private val getAllPodcastsUseCase: GetAllPodcastUseCase,
    private val appPreferencesUseCase: PresentationPreferences

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