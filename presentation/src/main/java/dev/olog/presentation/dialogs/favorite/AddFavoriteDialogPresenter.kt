package dev.olog.presentation.dialogs.favorite

import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.interactor.AddToFavoriteUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.toDomain
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val schedulers: Schedulers
) {

    suspend fun execute(mediaId: PresentationId) = withContext(schedulers.io) {
        val type = if (mediaId.isAnyPodcast) FavoriteTrackType.PODCAST else FavoriteTrackType.TRACK
        addToFavoriteUseCase(AddToFavoriteUseCase.Input(mediaId.toDomain(), type))
    }

}