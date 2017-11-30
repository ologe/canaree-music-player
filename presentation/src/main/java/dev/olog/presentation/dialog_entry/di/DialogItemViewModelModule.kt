package dev.olog.presentation.dialog_entry.di

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.detail.item.*
import dev.olog.presentation.dialog_entry.DialogItemFragment
import dev.olog.presentation.dialog_entry.DialogItemViewModel
import dev.olog.presentation.dialog_entry.DialogItemViewModelFactory
import dev.olog.presentation.dialog_entry.toDialogItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable

@Module
class DialogItemViewModelModule {

    @Provides
    fun provideViewModel(fragment: DialogItemFragment, factory: DialogItemViewModelFactory) : DialogItemViewModel {
        return ViewModelProviders.of(fragment, factory).get(DialogItemViewModel::class.java)
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderItem(mediaId: String, useCase: GetFolderUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistItem(mediaId: String, useCase: GetPlaylistUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALL)
    internal fun provideSongItem(mediaId: String, useCase: GetSongUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumItem(mediaId: String, useCase: GetAlbumUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistItem(mediaId: String, useCase: GetArtistUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreItem(mediaId: String, useCase: GetGenreUseCase) : Flowable<DisplayableItem> {
        return useCase.execute(mediaId).map { it.toDialogItem() }
    }

    // use cases


    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.VIEW_ALBUM)
    fun provideViewAlbum(navigator: Navigator, mediaId: String,
                         getSongUseCase: GetSongUseCase) : Completable {

        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_ALL -> getSongUseCase.execute(mediaId)
                    .map { MediaIdHelper.albumId(it.albumId) }
                    .flatMapCompletable { albumMediaId ->
                        CompletableSource { navigator.toDetailActivity(albumMediaId, 0) } // todo position
                    }
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.VIEW_ARTIST)
    fun provideViewArtist(navigator: Navigator, mediaId: String,
                          getSongUseCase: GetSongUseCase,
                          getAlbumUseCase: GetAlbumUseCase) : Completable {

        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_ALL -> getSongUseCase.execute(mediaId)
                    .map { MediaIdHelper.artistId(it.artistId) }
                    .flatMapCompletable { artistMediaId ->
                        CompletableSource { navigator.toDetailActivity(artistMediaId, 0) } // todo position
                    }
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> getAlbumUseCase.execute(mediaId)
                    .map { MediaIdHelper.artistId(it.artistId) }
                    .flatMapCompletable { artistMediaId ->
                        CompletableSource { navigator.toDetailActivity(artistMediaId, 0) } // todo position
                    }

            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.SHARE)
    fun provideShare(activity: AppCompatActivity, mediaId: String, useCase: GetSongUseCase) : Completable {
        return useCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { share(activity, it) }
                .toCompletable()
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.SET_RINGTONE)
    fun provideSetRingtone(navigator: Navigator, mediaId: String) : Completable {
        return Completable.fromCallable { navigator.toSetRingtoneDialog(mediaId) }
    }

    private fun share(activity: AppCompatActivity, song: Song){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + song.path))
        intent.type = "audio/*"
        if (intent.resolveActivity(activity.packageManager) != null){
            activity.startActivity(Intent.createChooser(intent, "share ${song.title}?"))
        } else {
            Log.e("DialogItem", "share failed, null package manager")
        }
    }

}