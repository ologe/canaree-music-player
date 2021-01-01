package dev.olog.feature.dialog.favorite

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.Favorite
import dev.olog.domain.interactor.AddToFavoriteUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument

class AddFavoriteDialogViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)

    suspend fun execute() {
        addToFavoriteUseCase(
            mediaId = mediaId,
            type = Favorite.Type.fromMediaId(mediaId)
        )
    }

}