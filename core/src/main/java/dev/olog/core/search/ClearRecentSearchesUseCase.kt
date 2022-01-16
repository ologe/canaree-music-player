package dev.olog.core.search

import dev.olog.core.search.RecentSearchesGateway
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

) {

    suspend operator fun invoke() {
        recentSearchesGateway.deleteAll()
    }
}