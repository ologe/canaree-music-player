package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.PodcastAlbumGateway2
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.presentation.dagger.MediaIdCategoryKey
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapToList
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable

@Module
class DetailFragmentModulePodcastAlbum {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePodcastPlaylist(
            resources: Resources,
            mediaId: MediaId,
            useCase: PodcastPlaylistGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeSiblings(mediaId.resolveId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun providePodcastAlbum(
            resources: Resources,
            mediaId: MediaId,
            useCase: PodcastAlbumGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeSiblings(mediaId.categoryId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ARTISTS)
    internal fun providePodcastArtist(
            resources: Resources,
            mediaId: MediaId,
            useCase: PodcastAlbumGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeArtistsAlbums(mediaId.categoryId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }

}