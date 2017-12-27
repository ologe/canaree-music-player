//package dev.olog.presentation.dialog_add_playlist
//
//import android.app.Application
//import android.text.TextUtils
//import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
//import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
//import dev.olog.presentation.R
//import io.reactivex.Completable
//import io.reactivex.Single
//import org.jetbrains.anko.toast
//import javax.inject.Inject
//
//class AddPlaylistPresenter @Inject constructor(
//        private val application: Application,
//        private val mediaId: String,
//        private val getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
//        private val addToPlaylistUseCase: AddToPlaylistUseCase
//
//) {
//
//    fun getPlaylistsAsList(): List<DisplayablePlaylist> {
//        return getPlaylistBlockingUseCase.execute()
//                .map { DisplayablePlaylist(it.id, "- ${it.title}") }
//    }
//
//    fun onItemClick(position: Int): Completable {
//
////        return Single.fromCallable { getPlaylistBlockingUseCase.execute()[position] }
////                .flatMap { playlist -> addToPlaylistUseCase.execute(Pair(playlist, mediaId)) }
////                .doOnSuccess { createSuccessMessage(it) }
////                .doOnError { createErrorMessage() }
////                .toCompletable()
//        return Completable.complete()
//    }
//
//
//
//}