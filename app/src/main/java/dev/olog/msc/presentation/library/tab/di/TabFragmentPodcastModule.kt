package dev.olog.msc.presentation.library.tab.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.interactor.all.GetAllPodcastAlbumsUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastArtistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastPlaylistUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.defer
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable

@Suppress("unused")
@Module
class TabFragmentPodcastModule {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePodcastPlaylist(
            resources: Resources,
            podcastUseCase: GetAllPodcastPlaylistUseCase

    ): Observable<List<DisplayableItem>>{
        return podcastUseCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .defer()
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
            resources: Resources): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun providePodcastAlbumData(
            useCase: GetAllPodcastAlbumsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
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

private fun Podcast.toTabDisplayableItem(): DisplayableItem {
    val artist = DisplayableItem.adjustArtist(this.artist)
    val album = DisplayableItem.adjustAlbum(this.album)

    return DisplayableItem(
            R.layout.item_tab_song,
            MediaId.podcastId(this.id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true
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