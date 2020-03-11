package dev.olog.presentation.dialogs.playlist.rename

import dev.olog.core.interactor.playlist.RenameUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.toDomain
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RenameDialogPresenter @Inject constructor(
    private val renameUseCase: RenameUseCase,
    private val schedulers: Schedulers
) {


    suspend fun execute(
        mediaId: PresentationId.Category,
        newTitle: String
    ) = withContext(schedulers.io){
        renameUseCase(mediaId.toDomain(), newTitle)
    }

}