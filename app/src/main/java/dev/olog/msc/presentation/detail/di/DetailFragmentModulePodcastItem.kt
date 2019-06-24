package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.msc.domain.interactor.item.GetPodcastAlbumUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastArtistUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastPlaylistUseCase
import dev.olog.presentation.dagger.MediaIdCategoryKey
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.asFlowable
import io.reactivex.Flowable

@Module
class DetailFragmentModulePodcastItem {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePlaylistItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetPodcastPlaylistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun provideAlbumItem(
        mediaId: MediaId,
        useCase: GetPodcastAlbumUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ARTISTS)
    internal fun provideArtistItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetPodcastArtistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

}