package dev.olog.presentation.dialogs.playlist.duplicates

import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.RemoveDuplicatesUseCase
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveDuplicatesDialogPresenter @Inject constructor(
    private val useCase: RemoveDuplicatesUseCase,
    private val schedulers: Schedulers
) {

    suspend fun execute(mediaId: MediaId) = withContext(schedulers.io){
        useCase(mediaId)
    }

}