package dev.olog.feature.dialog.delete

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.interactor.DeleteUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument

class DeleteDialogViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val deleteUseCase: DeleteUseCase,
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)

    suspend fun execute() = deleteUseCase(mediaId)

}