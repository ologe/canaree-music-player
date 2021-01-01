package dev.olog.feature.dialog.playlist.clear

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.interactor.playlist.ClearPlaylistUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument

class ClearPlaylistDialogViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val useCase: ClearPlaylistUseCase,
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString) as MediaId.Category

    suspend fun execute() = useCase(mediaId)

}