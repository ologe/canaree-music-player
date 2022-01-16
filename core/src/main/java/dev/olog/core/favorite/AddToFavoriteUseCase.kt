package dev.olog.core.favorite

import dev.olog.core.MediaUri
import dev.olog.core.interactor.GetTracksByCategoryUseCase
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway,
    private val tracksByCategoryUseCase: GetTracksByCategoryUseCase,
) {

    suspend operator fun invoke(uri: MediaUri) {
        val uris = when (uri.category) {
            MediaUri.Category.Track -> listOf(uri)
            else -> tracksByCategoryUseCase(uri).map { it.uri }
        }
        favoriteGateway.add(uris)
    }

}