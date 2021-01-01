package dev.olog.feature.dialog.delete

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.interactor.DeleteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteDialogPresenter @Inject constructor(
    private val deleteUseCase: DeleteUseCase
) {


    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        deleteUseCase(mediaId)
    }

}