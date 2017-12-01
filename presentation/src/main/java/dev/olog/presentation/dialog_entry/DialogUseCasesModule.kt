package dev.olog.presentation.dialog_entry

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Singles

@Module(includes = [(ItemModule::class)])
class DialogUseCasesModule (
        private val mediaId: String
) {

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.ADD_PLAYLIST)
    fun provideAddToPlaylistUseCase(
            navigator: Navigator,
            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
            getSongListByParamUseCase: GetSongListByParamUseCase): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
            listSize.to(displayableItem.title)

        })
                .doOnSuccess { navigator.toAddToPlaylistDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.ADD_FAVORITE)
    fun provideAddToFavoriteUseCase(
            getSongListByParamUseCase: GetSongListByParamUseCase,
            navigator: Navigator,
            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
            listSize.to(displayableItem.title)
        })
                .doOnSuccess { navigator.toAddToFavoriteDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.ADD_QUEUE)
    fun provideAddQueueUseCase(getSongListByParamUseCase: GetSongListByParamUseCase,
                               item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
                               navigator: Navigator): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
            listSize.to(displayableItem.title)

        })
                .doOnSuccess { navigator.toAddToQueueDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.VIEW_ALBUM)
    fun provideViewAlbumUseCase(
            getSongUseCase: GetSongUseCase,
            navigator: Navigator) : Completable {

        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_ALL -> getSongUseCase.execute(mediaId)
                    .map { MediaIdHelper.albumId(it.albumId) }
                    .firstOrError()
                    .doOnSuccess { navigator.toDetailActivity(it, 0) }
                    .toCompletable()
            else -> Completable.complete()
        }
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.VIEW_ARTIST)
    fun provideViewArtistUseCase(
            getSongUseCase: GetSongUseCase,
            getAlbumUseCase: GetAlbumUseCase,
            navigator: Navigator) : Completable {

        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_ALL -> getSongUseCase.execute(mediaId)
                    .map { MediaIdHelper.artistId(it.artistId) }
                    .firstOrError()
                    .doOnSuccess { navigator.toDetailActivity(it, 0) }
                    .toCompletable()

            MediaIdHelper.MEDIA_ID_BY_ALBUM -> getAlbumUseCase.execute(mediaId)
                    .map { MediaIdHelper.artistId(it.artistId) }
                    .firstOrError()
                    .doOnSuccess { navigator.toDetailActivity(it, 0) }
                    .toCompletable()

            else -> Completable.complete()
        }
    }


    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.SHARE)
    fun provideShareUseCase(
            getSongUseCase: GetSongUseCase,
            activity: AppCompatActivity) : Completable {

        return getSongUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { share(activity, it) }
                .toCompletable()
    }


    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.SET_RINGTONE)
    fun provideSetRingtoneUseCase(
            navigator: Navigator) : Completable {

        return Completable.fromCallable { navigator.toSetRingtoneDialog(mediaId) }
    }

    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.SHARE)
    private fun share(activity: AppCompatActivity, song: Song){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${song.path}"))
        intent.type = "audio/*"
        if (intent.resolveActivity(activity.packageManager) != null){
            activity.startActivity(Intent.createChooser(intent, "share ${song.title}?"))
        } else {
            Log.e("DialogItem", "share failed, null package manager")
        }
    }


    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.RENAME)
    fun provideRenamePlaylistUseCase(navigator: Navigator): Completable {

        return Completable.fromCallable { navigator.toRenameDialog(mediaId) }
    }


    @Provides
    @IntoMap
    @StringKey(DialogItemViewModel.DELETE)
    fun provideDeleteUseCase(
            getSongListByParamUseCase: GetSongListByParamUseCase,
            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
            navigator: Navigator): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
                    listSize.to(displayableItem.title)
                })
                .doOnSuccess { navigator.toDeleteDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

}