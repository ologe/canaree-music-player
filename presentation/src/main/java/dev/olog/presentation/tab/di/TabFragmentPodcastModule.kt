package dev.olog.presentation.tab.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaIdCategory
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.dagger.MediaIdCategoryKey
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.tab.TabFragmentHeaders
import io.reactivex.Observable

@Suppress("unused")
@Module
class TabFragmentPodcastModule {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePodcastPlaylist(
        resources: Resources,
//            podcastUseCase: GetAllPodcastPlaylistUseCase,
//            autoPlaylistUseCase: GetAllPodcastsAutoPlaylistUseCase,
        headers: TabFragmentHeaders

    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        val autoPlaylistObs = autoPlaylistUseCase.execute()
//                .mapToList { it.toAutoPlaylist() }
//                .map { it.startWith(headers.autoPlaylistHeader) }
//                .defer()
//
//        val playlistObs = podcastUseCase.execute()
//                .mapToList { it.toTabDisplayableItem(resources) }
//                .map { it.startWithIfNotEmpty(headers.allPlaylistHeader) }
//                .defer()
//
//        return Observables.combineLatest(playlistObs, autoPlaylistObs) { playlist, autoPlaylist ->
//            autoPlaylist.plus(playlist)
//        }.defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS)
    internal fun providePodcastData(@ApplicationContext context: Context)
            : Observable<List<DisplayableItem>> {
        return Observable.empty()
//        return useCase.execute()
//                .mapToList { it.toTabDisplayableItem(context) }
//                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ARTISTS)
    internal fun providePodcastArtistsData(
//            useCase: GetAllPodcastArtistsUseCase,
//            lastPlayedArtistsUseCase: GetLastPlayedPodcastArtistsUseCase,
//            newArtistsUseCase: GetRecentlyAddedPodcastsArtistsUseCase,
        resources: Resources,
        headers: TabFragmentHeaders
    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        val allObs = useCase.execute()
//                .mapToList { it.toTabDisplayableItem(resources) }
//                .map { it.toMutableList() }
//                .defer()
//
//        val lastPlayedObs = Observables.combineLatest(
//                lastPlayedArtistsUseCase.execute().distinctUntilChanged(),
//                newArtistsUseCase.execute().distinctUntilChanged()
//        ) { last, new ->
//            val result = mutableListOf<DisplayableItem>()
//            result.doIf(new.count() > 0) { addAll(headers.recentlyAddedArtistsHeaders) }
//                    .doIf(last.count() > 0) { addAll(headers.lastPlayedArtistHeaders) }
//                    .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
//        }.distinctUntilChanged()
//                .defer()
//
//        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
//                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun providePodcastAlbumData(

        headers: TabFragmentHeaders
    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        val allObs = useCase.execute()
//                .mapToList { it.toTabDisplayableItem() }
//                .map { it.toMutableList() }
//                .defer()
//
//        val lastPlayedObs = Observables.combineLatest(
//                lastPlayedAlbumsUseCase.execute().distinctUntilChanged(),
//                newAlbumsUseCase.execute().distinctUntilChanged()
//        ) { last, new ->
//            val result = mutableListOf<DisplayableItem>()
//            result.doIf(new.count() > 0) { addAll(headers.recentlyAddedAlbumsHeaders) }
//                    .doIf(last.count() > 0) { addAll(headers.lastPlayedAlbumHeaders) }
//                    .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
//        }.distinctUntilChanged()
//                .defer()
//
//        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
//                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_PODCAST_ALBUMS)
    internal fun provideLastPlayedAlbumData(
//            useCase: GetLastPlayedPodcastAlbumsUseCase
    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        return useCase.execute()
//                .mapToList { it.toTabLastPlayedDisplayableItem() }
//                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_PODCAST_ARTISTS)
    internal fun provideLastPlayedArtistData(
        resources: Resources
//            useCase: GetLastPlayedPodcastArtistsUseCase
    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        return useCase.execute()
//                .mapToList { it.toTabLastPlayedDisplayableItem(resources) }
//                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.NEW_PODCSAT_ALBUMS)
    internal fun provideNewAlbumsData(
//            useCase: GetRecentlyAddedPodcastsAlbumsUseCase
    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        return useCase.execute()
//                .mapToList { it.toTabLastPlayedDisplayableItem() }
//                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.NEW_PODCSAT_ARTISTS)
    internal fun provideNewArtistsData(
        resources: Resources
//            useCase: GetRecentlyAddedPodcastsArtistsUseCase
    ): Observable<List<DisplayableItem>> {
        return Observable.empty()
//        return useCase.execute()
//                .mapToList { it.toTabLastPlayedDisplayableItem(resources) }
//                .defer()
    }

}