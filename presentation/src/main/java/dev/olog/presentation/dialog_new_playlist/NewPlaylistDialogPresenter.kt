package dev.olog.presentation.dialog_new_playlist

import android.support.annotation.StringRes
import android.text.TextUtils
import dev.olog.domain.interactor.dialog.GetActualPlaylistUseCase
import dev.olog.presentation.R
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
        getPlaylistSiblingsUseCase: GetActualPlaylistUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase.execute()
            .map { it.title }
            .map { it.toLowerCase() }

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