package dev.olog.domain.interactor

import dev.olog.domain.gateway.spotify.SpotifyGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SpotifyFetcherUseCase @Inject constructor(
    private val spotifyGateway: SpotifyGateway
) {

    fun fetch() {
        spotifyGateway.fetchTracks()
    }

    fun observeStatus(): Flow<Int> {
        return spotifyGateway.observeFetchStatus()
    }

}