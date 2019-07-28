package dev.olog.core.interactor.search

import dev.olog.core.gateway.RecentSearchesGateway
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

) {

    suspend operator fun invoke() {
        recentSearchesGateway.deleteAll()
    }
}