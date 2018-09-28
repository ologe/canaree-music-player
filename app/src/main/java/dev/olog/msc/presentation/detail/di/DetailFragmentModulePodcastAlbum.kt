package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.interactor.all.sibling.GetPodcastAlbumSiblingsByAlbumUseCase
import dev.olog.msc.domain.interactor.all.sibling.GetPodcastAlbumSiblingsByArtistUseCase
import dev.olog.msc.domain.interactor.all.sibling.GetPodcastPlaylistsSiblingsUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable

@Module
class DetailFragmentModulePodcastAlbum {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePodcastPlaylist(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPodcastPlaylistsSiblingsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun providePodcastAlbum(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPodcastAlbumSiblingsByAlbumUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ARTISTS)
    internal fun providePodcastArtist(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPodcastAlbumSiblingsByArtistUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }

}

private fun PodcastPlaylist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.podcastPlaylistId(id),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun PodcastAlbum.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.podcastAlbumId(id),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase(),
            image
    )
}