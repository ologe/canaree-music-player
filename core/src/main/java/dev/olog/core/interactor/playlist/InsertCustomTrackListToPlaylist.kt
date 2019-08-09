package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val imageVersionGateway: ImageVersionGateway

) {

    suspend operator fun invoke(param: InsertCustomTrackListRequest) {
        val category = if (param.type == PlaylistType.TRACK) MediaIdCategory.PODCASTS
        else MediaIdCategory.PODCASTS_PLAYLIST

        if (param.type == PlaylistType.PODCAST) {
            val playlistId = podcastPlaylistGateway.createPlaylist(param.playlistTitle)

            val mediaId = MediaId.createCategoryValue(category, playlistId.toString())
            imageVersionGateway.increaseCurrentVersion(mediaId)

            podcastPlaylistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        } else {
            val playlistId = playlistGateway.createPlaylist(param.playlistTitle)

            val mediaId = MediaId.createCategoryValue(category, playlistId.toString())
            imageVersionGateway.increaseCurrentVersion(mediaId)

            playlistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        }
    }
}

class InsertCustomTrackListRequest(
    @JvmField
    val playlistTitle: String,
    @JvmField
    val tracksId: List<Long>,
    @JvmField
    val type: PlaylistType
)