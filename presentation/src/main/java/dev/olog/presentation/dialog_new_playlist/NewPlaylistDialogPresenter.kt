package dev.olog.presentation.dialog_new_playlist

import android.app.Application
import android.support.annotation.StringRes
import android.text.TextUtils
import dev.olog.domain.entity.Playlist
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.CreatePlaylistUseCase
import dev.olog.domain.interactor.dialog.GetActualPlaylistUseCase
import dev.olog.presentation.R
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: String,
        getPlaylistSiblingsUseCase: GetActualPlaylistUseCase,
        private val createPlaylistUseCase: CreatePlaylistUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase.execute()
            .map { it.title }
            .map { it.toLowerCase() }

    fun execute(playlistTitle: String) : Completable {

        return createPlaylistUseCase.execute(playlistTitle)
                .map { Playlist(it, playlistTitle, -1, "") }
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

    @StringRes
    fun checkData(playlistTitle: String): Int {
        if (TextUtils.isEmpty(playlistTitle)) {
            return R.string.popup_playlist_name_not_valid
        } else if (existingPlaylists.contains(playlistTitle.toLowerCase())) {
            return R.string.popup_playlist_name_already_exist
        }
        return 0
    }

}