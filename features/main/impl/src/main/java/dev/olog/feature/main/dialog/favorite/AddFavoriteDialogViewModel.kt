package dev.olog.feature.main.dialog.favorite

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.interactor.AddToFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddFavoriteDialogViewModel @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        addToFavoriteUseCase(AddToFavoriteUseCase.Input(mediaId, type))
    }

}