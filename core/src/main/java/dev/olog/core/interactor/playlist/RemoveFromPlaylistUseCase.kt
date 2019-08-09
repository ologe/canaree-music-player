package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway,
    private val imageVersionGateway: ImageVersionGateway

) {

    suspend operator fun invoke(input: Input) = withContext(Dispatchers.Default){
        val category = if (input.type == PlaylistType.TRACK) MediaIdCategory.PODCASTS
        else MediaIdCategory.PODCASTS_PLAYLIST

        val mediaId = MediaId.createCategoryValue(category, input.playlistId.toString())
        imageVersionGateway.increaseCurrentVersion(mediaId)

        if (input.type == PlaylistType.PODCAST){
            podcastGateway.removeFromPlaylist(input.playlistId, input.idInPlaylist)
        } else {
            playlistGateway.removeFromPlaylist(input.playlistId, input.idInPlaylist)
        }
    }

    class Input(
            val playlistId: Long,
            val idInPlaylist: Long,
            val type: PlaylistType
    )

}