package dev.olog.presentation.dialogs.favorite

import dev.olog.domain.entity.favorite.FavoriteTrackType
import dev.olog.domain.interactor.AddToFavoriteUseCase
import dev.olog.domain.schedulers.Schedulers
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