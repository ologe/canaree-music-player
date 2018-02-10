package dev.olog.msc.presentation.dialog.new.playlist

import android.app.Application
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.CreatePlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        getPlaylistSiblingsUseCase: GetPlaylistBlockingUseCase,
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
        val message = if (android.text.TextUtils.isDigitsOnly(string)){
            val size = string.toInt()
            application.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, size, size, playlistTitle)
        } else {
            application.getString(R.string.added_song_x_to_playlist_y, string, playlistTitle)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

    fun isStringValid(string: String): Boolean {
        return !existingPlaylists.contains(string.toLowerCase())
    }

}