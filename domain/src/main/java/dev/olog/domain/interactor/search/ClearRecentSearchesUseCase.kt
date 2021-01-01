package dev.olog.domain.interactor.search

import dev.olog.domain.gateway.RecentSearchesGateway
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

) {

    suspend operator fun invoke() {
        recentSearchesGateway.deleteAll()
    }
}