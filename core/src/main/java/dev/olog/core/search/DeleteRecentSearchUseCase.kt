package dev.olog.core.search

import dev.olog.core.MediaUri
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway,
) {

    suspend operator fun invoke(uri: MediaUri) {
        recentSearchesGateway.delete(uri)
    }
}