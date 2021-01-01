package dev.olog.domain.interactor.playlist

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.Playlist
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(playlist: Playlist, mediaId: MediaId) {
        if (mediaId.isLeaf && mediaId.isPodcast) {
            podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId))
            return
        }

        if (mediaId.isLeaf) {
            playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId))
            return
        }

        val songList = getSongListByParamUseCase(mediaId).map { it.id }
        if (mediaId.isAnyPodcast) {
            podcastPlaylistGateway.addSongsToPlaylist(playlist.id, songList)
        } else {
            playlistGateway.addSongsToPlaylist(playlist.id, songList)
        }
    }
}