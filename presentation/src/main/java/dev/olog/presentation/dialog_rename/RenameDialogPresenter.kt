package dev.olog.presentation.dialog_rename

import android.app.Application
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.domain.interactor.dialog.RenameUseCase
import dev.olog.presentation.R
import dev.olog.shared.MediaId
import io.reactivex.Completable
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class RenameDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        getPlaylistSiblingsUseCase: GetPlaylistBlockingUseCase,
        private val renameUseCase: RenameUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase.execute()
            .map { it.title }
            .map { it.toLowerCase() }

    fun execute(oldTitle: String, newTitle: String) : Completable {
        return renameUseCase.execute(Pair(mediaId, newTitle))
                .doOnComplete { createSuccessMessage(oldTitle, newTitle) }
                .doOnError { createErrorMessage() }
    }

    private fun createSuccessMessage(oldTitle: String, newTitle: String){
        val stringId = when {
            mediaId.isPlaylist -> R.string.playlist_x_renamed_to_y
            mediaId.isFolder -> R.string.folder_x_renamed_to_y
            else -> 0
        }
        if (stringId != 0){
            val message = application.getString(stringId, oldTitle, newTitle)
            application.toast(message)
        }
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

    /**
     * returns false if is invalid
     */
    fun checkData(playlistTitle: String): Boolean {
        return when {
            mediaId.isPlaylist -> !existingPlaylists.contains(playlistTitle.toLowerCase())
            mediaId.isFolder -> {
                val folderPath = mediaId.categoryValue
                val parent = File(folderPath).parent
                return !File(parent, playlistTitle).exists()
            }
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

}