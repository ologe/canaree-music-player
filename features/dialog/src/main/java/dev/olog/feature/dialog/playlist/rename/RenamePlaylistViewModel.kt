package dev.olog.feature.dialog.playlist.rename

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.interactor.playlist.RenamePlaylistUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import javax.inject.Inject

class RenamePlaylistViewModel @Inject constructor(
    @Assisted private val state: SavedStateHandle,
    private val renameUseCase: RenamePlaylistUseCase
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString) as MediaId.Category

    suspend fun execute(newTitle: String) = renameUseCase(mediaId, newTitle)

}