package dev.olog.presentation.dialogs.playlist.duplicates

import dev.olog.domain.interactor.playlist.RemoveDuplicatesUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveDuplicatesDialogPresenter @Inject constructor(
    private val useCase: RemoveDuplicatesUseCase,
    private val schedulers: Schedulers
) {

    suspend fun execute(mediaId: PresentationId.Category) = withContext(schedulers.io){
        useCase(mediaId.toDomain())
    }

}