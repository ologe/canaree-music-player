package dev.olog.feature.dialogs.favorite

import dev.olog.core.MediaId
import dev.olog.core.interactor.AddToFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        addToFavoriteUseCase(mediaId)
    }

}