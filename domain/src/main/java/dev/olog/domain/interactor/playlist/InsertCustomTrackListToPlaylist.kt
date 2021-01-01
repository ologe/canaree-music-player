package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.shared.exhaustive
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend operator fun invoke(
        playlistTitle: String,
        type: PlaylistType,
        vararg tracksId: Long,
    ) {
        when (type) {
            PlaylistType.TRACK -> {
                val playlistId = playlistGateway.createPlaylist(playlistTitle)
                playlistGateway.addSongsToPlaylist(playlistId, *tracksId)
            }
            PlaylistType.PODCAST -> {
                val playlistId = podcastPlaylistGateway.createPlaylist(playlistTitle)
                podcastPlaylistGateway.addSongsToPlaylist(playlistId, *tracksId)
            }
            PlaylistType.AUTO -> error("invalid type=$type")
        }.exhaustive
    }
}