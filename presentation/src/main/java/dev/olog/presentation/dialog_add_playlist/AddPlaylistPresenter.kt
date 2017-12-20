package dev.olog.presentation.dialog_add_playlist

import android.app.Application
import android.text.TextUtils
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetActualPlaylistUseCase
import dev.olog.presentation.R
import io.reactivex.Completable
import io.reactivex.Single
import org.jetbrains.anko.toast
import javax.inject.Inject

class AddPlaylistPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: String,
        private val getPlaylistSiblingsUseCase: GetActualPlaylistUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) {

    fun getPlaylistsAsList(): List<DisplayablePlaylist> {
        return getPlaylistSiblingsUseCase.execute()
                .map { DisplayablePlaylist(it.id, "- ${it.title}") }
    }

    fun onItemClick(position: Int): Completable {

        return Single.fromCallable { getPlaylistSiblingsUseCase.execute()[position] }
                .flatMap { playlist -> addToPlaylistUseCase.execute(Pair(playlist, mediaId)) }
                .doOnSuccess { createSuccessMessage(it) }
                .doOnError { createErrorMessage() }
                .toCompletable()
    }

    private fun createSuccessMessage(pairStringPlaylistName: Pair<String, String>){
        val (string, playlistTitle) = pairStringPlaylistName
        val message = if (TextUtils.isDigitsOnly(string)){
            val size = string.toInt()
            application.resources.getQuantityString(R.plurals.added_xx_songs_to_playlist_y, size, size, playlistTitle)
        } else {
            application.getString(R.string.added_song_x_to_playlist_y, string, playlistTitle)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

}

data class DisplayablePlaylist(
        val playlistId: Long,
        val playlistTitle: String
)