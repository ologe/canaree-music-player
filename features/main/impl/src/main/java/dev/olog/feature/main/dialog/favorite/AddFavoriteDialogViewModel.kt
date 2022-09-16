package dev.olog.feature.main.dialog.favorite

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.AddToFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddFavoriteDialogViewModel @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        addToFavoriteUseCase(mediaId)
    }

}