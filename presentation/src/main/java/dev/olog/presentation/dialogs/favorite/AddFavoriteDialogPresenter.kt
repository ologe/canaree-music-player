package dev.olog.presentation.dialogs.favorite

import dev.olog.core.MediaId
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.interactor.AddToFavoriteUseCase
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val schedulers: Schedulers
) {

    suspend fun execute(mediaId: MediaId) = withContext(schedulers.io) {
        val type = if (mediaId.isAnyPodcast) FavoriteTrackType.PODCAST else FavoriteTrackType.TRACK
        addToFavoriteUseCase(AddToFavoriteUseCase.Input(mediaId, type))
    }

}