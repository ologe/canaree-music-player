package dev.olog.presentation.dialog_add_playlist

import dev.olog.domain.interactor.dialog.GetActualPlaylistUseCase
import javax.inject.Inject

class AddPlaylistPresenter @Inject constructor(
        private val mediaId: String,
        private val getPlaylistSiblingsUseCase: GetActualPlaylistUseCase

) {

    fun getPlaylistsAsList(): List<DisplayablePlaylist> {
        return getPlaylistSiblingsUseCase.execute()
                .map { DisplayablePlaylist(it.id, "- ${it.title}") }
    }

    fun onItemClick(position: Int){

    }

}

data class DisplayablePlaylist(
        val playlistId: Long,
        val playlistTitle: String
)