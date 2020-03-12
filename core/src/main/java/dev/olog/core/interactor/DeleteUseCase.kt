package dev.olog.core.interactor

import dev.olog.core.MediaId
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
        if (mediaId.isLeaf) {
            return trackGateway.deleteSingle(mediaId.leaf!!)
        }

        return when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.deletePlaylist(mediaId.categoryId)
            mediaId.isPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryId)
            else -> {
                val songList = getSongListByParamUseCase(mediaId)
                trackGateway.deleteGroup(songList.map { it.id })
            }
        }
    }
}