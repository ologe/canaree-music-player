package dev.olog.msc.presentation.library.tab.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaIdCategory
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.interactor.all.*
import dev.olog.msc.domain.interactor.all.last.played.GetLastPlayedPodcastAlbumsUseCase
import dev.olog.msc.domain.interactor.all.last.played.GetLastPlayedPodcastArtistsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedPodcastsAlbumsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedPodcastsArtistsUseCase
import dev.olog.msc.presentation.library.tab.TabFragmentHeaders
import dev.olog.msc.presentation.library.tab.mapper.toAutoPlaylist
import dev.olog.msc.presentation.library.tab.mapper.toTabDisplayableItem
import dev.olog.msc.presentation.library.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.shared.defer
import dev.olog.shared.mapToList
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.doIf
import dev.olog.shared.startWith
import dev.olog.shared.startWithIfNotEmpty
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

@Suppress("unused")
@Module
class TabFragmentPodcastModule {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePodcastPlaylist(
            resources: Resources,
            podcastUseCase: GetAllPodcastPlaylistUseCase,
            autoPlaylistUseCase: GetAllPodcastsAutoPlaylistUseCase,
            headers: TabFragmentHeaders

    ): Observable<List<DisplayableItem>>{

        val autoPlaylistObs = autoPlaylistUseCase.execute()
                .mapToList { it.toAutoPlaylist() }
                .map { it.startWith(headers.autoPlaylistHeader) }
                .defer()

        val playlistObs = podcastUseCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map { it.startWithIfNotEmpty(headers.allPlaylistHeader) }
                .defer()

        return Observables.combineLatest(playlistObs, autoPlaylistObs) { playlist, autoPlaylist ->
            autoPlaylist.plus(playlist)
        }.defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS)
    internal fun providePodcastData(@ApplicationContext context: Context, useCase: GetAllPodcastUseCase)
            : Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem(context) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ARTISTS)
    internal fun providePodcastArtistsData(
            useCase: GetAllPodcastArtistsUseCase,
            lastPlayedArtistsUseCase: GetLastPlayedPodcastArtistsUseCase,
            newArtistsUseCase: GetRecentlyAddedPodcastsArtistsUseCase,
            resources: Resources,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map { it.toMutableList() }
                .defer()

        val lastPlayedObs = Observables.combineLatest(
                lastPlayedArtistsUseCase.execute().distinctUntilChanged(),
                newArtistsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.recentlyAddedArtistsHeaders) }
                    .doIf(last.count() > 0) { addAll(headers.lastPlayedArtistHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
        }.distinctUntilChanged()
                .defer()

        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun providePodcastAlbumData(
            useCase: GetAllPodcastAlbumsUseCase,
            lastPlayedAlbumsUseCase: GetLastPlayedPodcastAlbumsUseCase,
            newAlbumsUseCase: GetRecentlyAddedPodcastsAlbumsUseCase,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map { it.toMutableList() }
                .defer()

        val lastPlayedObs = Observables.combineLatest(
                lastPlayedAlbumsUseCase.execute().distinctUntilChanged(),
                newAlbumsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.recentlyAddedAlbumsHeaders) }
                    .doIf(last.count() > 0) { addAll(headers.lastPlayedAlbumHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
        }.distinctUntilChanged()
                .defer()

        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_PODCAST_ALBUMS)
    internal fun provideLastPlayedAlbumData(
            useCase: GetLastPlayedPodcastAlbumsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem() }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_PODCAST_ARTISTS)
    internal fun provideLastPlayedArtistData(
            resources: Resources,
            useCase: GetLastPlayedPodcastArtistsUseCase) : Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem(resources) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.NEW_PODCSAT_ALBUMS)
    internal fun provideNewAlbumsData(
            useCase: GetRecentlyAddedPodcastsAlbumsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem() }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.NEW_PODCSAT_ARTISTS)
    internal fun provideNewArtistsData(
            resources: Resources,
            useCase: GetRecentlyAddedPodcastsArtistsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem(resources) }
                .defer()
    }

}