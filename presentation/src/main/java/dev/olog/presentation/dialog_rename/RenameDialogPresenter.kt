package dev.olog.presentation.dialog_rename

import android.app.Application
import android.support.annotation.StringRes
import android.text.TextUtils
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.domain.interactor.dialog.RenamePlaylistUseCase
import dev.olog.presentation.R
import dev.olog.shared.MediaId
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

class RenameDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        getPlaylistSiblingsUseCase: GetPlaylistBlockingUseCase,
        private val renamePlaylistUseCase: RenamePlaylistUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase.execute()
            .map { it.title }
            .map { it.toLowerCase() }

    fun execute(oldTitle: String, newTitle: String) : Completable {
        val playlistId = mediaId.categoryValue.toLong()
        return renamePlaylistUseCase.execute(Pair(playlistId, newTitle))
                .doOnComplete { createSuccessMessage(oldTitle, newTitle) }
                .doOnError { createErrorMessage() }
    }

    private fun createSuccessMessage(oldTitle: String, newTitle: String){
        val message = application.getString(R.string.playlist_x_renamed_to_y, oldTitle, newTitle)
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