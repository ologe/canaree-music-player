package dev.olog.msc.presentation.library.tab.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.app.app
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.interactor.all.*
import dev.olog.msc.domain.interactor.all.last.played.GetLastPlayedPodcastAlbumsUseCase
import dev.olog.msc.domain.interactor.all.last.played.GetLastPlayedPodcastArtistsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedPodcastsAlbumsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedPodcastsArtistsUseCase
import dev.olog.msc.presentation.library.tab.TabFragmentHeaders
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

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
    internal fun providePodcastData(useCase: GetAllPodcastUseCase)
            : Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
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
            result.doIf(new.count() > 0) { addAll(headers.newArtistsHeaders) }
                    .doIf(last.count() > 0) { addAll(headers.recentArtistHeaders) }
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
            result.doIf(new.count() > 0) { addAll(headers.newAlbumsHeaders) }
                    .doIf(last.count() > 0) { addAll(headers.recentAlbumHeaders) }
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

private fun PodcastPlaylist.toTabDisplayableItem(resources: Resources): DisplayableItem {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.podcastPlaylistId(id),
            title,
            size,
            this.image
    )
}


private fun PodcastPlaylist.toAutoPlaylist(): DisplayableItem {

    return DisplayableItem(
            R.layout.item_tab_auto_playlist,
            MediaId.podcastPlaylistId(id),
            title,
            "",
            this.image
    )
}

private fun Podcast.toTabDisplayableItem(): DisplayableItem {
    val artist = DisplayableItem.adjustArtist(this.artist)

    val duration = app.getString(R.string.tab_podcast_duration, TimeUnit.MILLISECONDS.toMinutes(this.duration))

    return DisplayableItem(
            R.layout.item_tab_podcast,
            MediaId.podcastId(this.id),
            title,
            artist,
            image,
            trackNumber = duration,
            isPlayable = true
    )
}

private fun PodcastArtist.toTabDisplayableItem(resources: Resources): DisplayableItem{
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
            R.layout.item_tab_artist,
            MediaId.podcastArtistId(id),
            name,
            albums + songs,
            this.image
    )
}


private fun PodcastAlbum.toTabDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.podcastAlbumId(id),
            title,
            DisplayableItem.adjustArtist(artist),
            image
    )
}

private fun PodcastAlbum.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album_last_played,
            MediaId.podcastAlbumId(id),
            title,
            artist,
            image
    )
}

private fun PodcastArtist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
            R.layout.item_tab_artist_last_played,
            MediaId.podcastArtistId(id),
            name,
            albums + songs,
            this.image
    )
}