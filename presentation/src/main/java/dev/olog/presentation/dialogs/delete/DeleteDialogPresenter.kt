package dev.olog.presentation.dialogs.delete

import dev.olog.domain.interactor.DeleteUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.toDomain
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteDialogPresenter @Inject constructor(
    private val deleteUseCase: DeleteUseCase,
    private val schedulers: Schedulers
) {


    suspend fun execute(mediaId: PresentationId) = withContext(schedulers.io) {
        deleteUseCase(mediaId.toDomain())
    }

}