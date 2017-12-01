package dev.olog.presentation.dialog_entry.di

import dagger.Module

@Module
class DialogItemFragmentModule(
//        private val fragment: DialogItemFragment

) {

//    @Provides
//    fun provideMediaId(): String {
//        return fragment.arguments!!.getString(DialogItemFragment.ARGUMENTS_MEDIA_ID)
//    }

//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.ADD_PLAYLIST)
//    fun provideAddToPlaylistUseCase(
//            mediaId: String,
//            navigator: Navigator,
//            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
//            getSongListByParamUseCase: GetSongListByParamUseCase): Completable {
//
//        return Singles.zip(
//                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
//                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
//            listSize.to(displayableItem.title)
//
//        })
//                .doOnSuccess { navigator.toAddToPlaylistDialog(mediaId, it.first, it.second) }
//                .toCompletable()
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.ADD_FAVORITE)
//    fun provideAddToFavoriteUseCase(
//            mediaId: String,
//            getSongListByParamUseCase: GetSongListByParamUseCase,
//            navigator: Navigator,
//            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>): Completable {
//
//        return Singles.zip(
//                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
//                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
//            listSize.to(displayableItem.title)
//        })
//                .doOnSuccess { navigator.toAddToFavoriteDialog(mediaId, it.first, it.second) }
//                .toCompletable()
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.ADD_QUEUE)
//    fun provideAddQueueUseCase(
//            mediaId: String,
//            getSongListByParamUseCase: GetSongListByParamUseCase,
//            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
//            navigator: Navigator): Completable {
//
//        return Singles.zip(
//                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
//                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
//            listSize.to(displayableItem.title)
//
//        })
//                .doOnSuccess { navigator.toAddToQueueDialog(mediaId, it.first, it.second) }
//                .toCompletable()
//    }

//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.VIEW_ALBUM)
//    fun provideViewAlbumUseCase(
//            mediaId: String,
//            getSongUseCase: GetSongUseCase,
//            navigator: Navigator) : Completable {
//
//        val category = MediaIdHelper.extractCategory(mediaId)
//        return when (category){
//            MediaIdHelper.MEDIA_ID_BY_ALL -> getSongUseCase.execute(mediaId)
//                    .map { MediaIdHelper.albumId(it.albumId) }
//                    .firstOrError()
//                    .doOnSuccess { navigator.toDetailActivity(it, 0) }
//                    .toCompletable()
//            else -> Completable.complete()
//        }
//    }

//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.VIEW_ARTIST)
//    fun provideViewArtistUseCase(
//            mediaId: String,
//            getSongUseCase: GetSongUseCase,
//            getAlbumUseCase: GetAlbumUseCase,
//            navigator: Navigator) : Completable {
//
//        val category = MediaIdHelper.extractCategory(mediaId)
//        return when (category){
//            MediaIdHelper.MEDIA_ID_BY_ALL -> getSongUseCase.execute(mediaId)
//                    .map { MediaIdHelper.artistId(it.artistId) }
//                    .firstOrError()
//                    .doOnSuccess { navigator.toDetailActivity(it, 0) }
//                    .toCompletable()
//
//            MediaIdHelper.MEDIA_ID_BY_ALBUM -> getAlbumUseCase.execute(mediaId)
//                    .map { MediaIdHelper.artistId(it.artistId) }
//                    .firstOrError()
//                    .doOnSuccess { navigator.toDetailActivity(it, 0) }
//                    .toCompletable()
//
//            else -> Completable.complete()
//        }
//    }


//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.SHARE)
//    fun provideShareUseCase(
//            mediaId: String,
//            getSongUseCase: GetSongUseCase,
//            activity: AppCompatActivity) : Completable {
//
//        return getSongUseCase.execute(mediaId)
//                .firstOrError()
//                .doOnSuccess { share(activity, it) }
//                .toCompletable()
//    }
//
//    private fun share(activity: AppCompatActivity, song: Song){
//        val intent = Intent()
//        intent.action = Intent.ACTION_SEND
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${song.path}"))
//        intent.type = "audio/*"
//        if (intent.resolveActivity(activity.packageManager) != null){
//            activity.startActivity(Intent.createChooser(intent, "share ${song.title}?"))
//        } else {
//            Log.e("DialogItem", "share failed, null package manager")
//        }
//    }


//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.SET_RINGTONE)
//    fun provideSetRingtoneUseCase(
//            mediaId: String,
//            navigator: Navigator) : Completable {
//
//        return Completable.fromCallable { navigator.toSetRingtoneDialog(mediaId) }
//    }


//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.RENAME)
//    fun provideRenamePlaylistUseCase(
//            mediaId: String,
//            navigator: Navigator): Completable {
//
//        return Completable.fromCallable { navigator.toRenameDialog(mediaId) }
//    }


//    @Provides
//    @IntoMap
//    @StringKey(DialogItemViewModel.DELETE)
//    fun provideDeleteUseCase(
//            mediaId: String,
//            getSongListByParamUseCase: GetSongListByParamUseCase,
//            item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
//            navigator: Navigator): Completable {
//
//        return Singles.zip(
//                getSongListByParamUseCase.execute(mediaId).map { it.size }.firstOrError(),
//                item[MediaIdHelper.extractCategory(mediaId)]!!.firstOrError(), { listSize, displayableItem ->
//                    listSize.to(displayableItem.title)
//                })
//                .doOnSuccess { navigator.toDeleteDialog(mediaId, it.first, it.second) }
//                .toCompletable()
//    }

}