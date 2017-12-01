package dev.olog.presentation.dialog_entry

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.*
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.rxkotlin.Singles
import javax.inject.Inject

class DialogUseCasesModule @Inject constructor(
        private val activity: AppCompatActivity,
        private val mediaId: String,
        private val navigator: Navigator,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        getFolderUseCase: GetFolderUseCase,
        getPlaylistUseCase: GetPlaylistUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        getArtistUseCase: GetArtistUseCase,
        getGenreUseCase: GetGenreUseCase
) {

    private val itemMap = mapOf(
            MediaIdHelper.MEDIA_ID_BY_FOLDER to getFolderUseCase.execute(mediaId).map { it.toDisplayableItem() },
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST to getPlaylistUseCase.execute(mediaId).map { it.toDisplayableItem() },
            MediaIdHelper.MEDIA_ID_BY_ALL to getSongUseCase.execute(mediaId).map { it.toDisplayableItem() },
            MediaIdHelper.MEDIA_ID_BY_ALBUM to getAlbumUseCase.execute(mediaId).map { it.toDisplayableItem() },
            MediaIdHelper.MEDIA_ID_BY_ARTIST to getArtistUseCase.execute(mediaId).map { it.toDisplayableItem() },
            MediaIdHelper.MEDIA_ID_BY_GENRE to getGenreUseCase.execute(mediaId).map { it.toDisplayableItem() }
    )

    fun provideAddToPlaylistUseCase(): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                itemMap[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
            listSize.to(displayableItem.title)
        })
                .doOnSuccess { navigator.toAddToPlaylistDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

    fun provideAddToFavoriteUseCase(): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                itemMap[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
            listSize.to(displayableItem.title)
        })
                .doOnSuccess { navigator.toAddToFavoriteDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

    fun provideAddQueueUseCase(): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                itemMap[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
            listSize.to(displayableItem.title)

        })
                .doOnSuccess { navigator.toAddToQueueDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

    fun provideViewAlbumUseCase() : Completable {

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

    fun provideViewArtistUseCase() : Completable {

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

    fun provideShareUseCase() : Completable {
        return getSongUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { share(activity, it) }
                .toCompletable()
    }

    fun provideSetRingtoneUseCase() : Completable {
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

    fun provideRenamePlaylistUseCase(): Completable {

        return Completable.fromCallable { navigator.toRenameDialog(mediaId) }
    }

    fun provideDeleteUseCase(): Completable {

        return Singles.zip(
                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
                itemMap[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
                    listSize.to(displayableItem.title)
                })
                .doOnSuccess { navigator.toDeleteDialog(mediaId, it.first, it.second) }
                .toCompletable()
    }

}