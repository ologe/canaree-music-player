package dev.olog.presentation.dialogs.favorite

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.AddToFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class AddFavoriteDialogViewModel @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) {
        addToFavoriteUseCase(mediaId)
    }

}