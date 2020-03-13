package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Track
import dev.olog.core.MediaIdCategory.PLAYLISTS
import dev.olog.core.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val trackGateway: TrackGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(mediaId: MediaId) {
        return when (mediaId) {
            is Track -> trackGateway.deleteSingle(mediaId.id)
            is Category -> handleCategory(mediaId)
        }
    }

    private suspend fun handleCategory(mediaId: Category) {
        return when (mediaId.category) {
            PODCASTS_PLAYLIST -> podcastPlaylistGateway.deletePlaylist(mediaId.categoryId)
            PLAYLISTS -> playlistGateway.deletePlaylist(mediaId.categoryId)
            else -> {
                val songList = getSongListByParamUseCase(mediaId)
                trackGateway.deleteGroup(songList.map { it.id })
            }
        }
    }

}