package dev.olog.feature.dialog.playlist.duplicates

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.interactor.playlist.RemovePlaylistDuplicatesUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import javax.inject.Inject

class RemovePlaylistDuplicatesDialogViewModel @Inject constructor(
    @Assisted private val state: SavedStateHandle,
    private val useCase: RemovePlaylistDuplicatesUseCase
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString) as MediaId.Category

    suspend fun execute() = useCase(mediaId)

}