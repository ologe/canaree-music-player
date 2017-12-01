package dev.olog.presentation.dialog_entry.di

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.interactor.detail.item.*
import dev.olog.presentation.fragment_detail.model.toHeaderItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable

@Module
class ItemModule {

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderItem(mediaId: String, useCase: GetFolderUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistItem(mediaId: String, useCase: GetPlaylistUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumItem(mediaId: String, useCase: GetAlbumUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistItem(mediaId: String, useCase: GetArtistUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreItem(mediaId: String, useCase: GetGenreUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
    }

}