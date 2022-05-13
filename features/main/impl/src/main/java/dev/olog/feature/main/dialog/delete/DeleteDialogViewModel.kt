package dev.olog.feature.main.dialog.delete

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.DeleteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeleteDialogViewModel @Inject constructor(
    private val deleteUseCase: DeleteUseCase
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        deleteUseCase(mediaId)
    }

}