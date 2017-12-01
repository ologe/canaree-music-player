package dev.olog.presentation.dialog_entry.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.interactor.detail.item.*
import dev.olog.presentation.dialog_entry.DialogModel
import dev.olog.presentation.dialog_entry.toDialogItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable

@Module
class DialogItemViewModelModule {

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderItem(mediaId: String, useCase: GetFolderUseCase) : Flowable<DialogModel> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistItem(mediaId: String, useCase: GetPlaylistUseCase) : Flowable<DialogModel> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALL)
    internal fun provideSongItem(@ApplicationContext context: Context,
                                 mediaId: String, useCase: GetSongUseCase) : Flowable<DialogModel> {

        return useCase.execute(mediaId).map { it.toDialogItem(context) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumItem(mediaId: String, useCase: GetAlbumUseCase) : Flowable<DialogModel> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistItem(mediaId: String, useCase: GetArtistUseCase) : Flowable<DialogModel> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreItem(mediaId: String, useCase: GetGenreUseCase) : Flowable<DialogModel> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }


}