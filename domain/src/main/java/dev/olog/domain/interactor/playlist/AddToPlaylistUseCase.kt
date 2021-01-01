package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.track.Playlist
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.shared.exhaustive
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    suspend operator fun invoke(
        playlist: Playlist,
        mediaId: MediaId
    ) {
        when (mediaId) {
            is MediaId.Category -> handleCategory(mediaId, playlist)
            is MediaId.Track -> handleTrack(mediaId, playlist)
        }.exhaustive
    }

    private suspend fun handleTrack(mediaId: MediaId.Track, playlist: Playlist) {
        if (mediaId.isAnyPodcast) {
            return podcastPlaylistGateway.addSongsToPlaylist(playlist.id, mediaId.id)
        }
        return playlistGateway.addSongsToPlaylist(playlist.id, mediaId.id)
    }

    private suspend fun handleCategory(mediaId: MediaId.Category, playlist: Playlist) {
        val songList = getSongListByParamUseCase(mediaId).map(Track::id)

        if (mediaId.isAnyPodcast) {
            return podcastPlaylistGateway.addSongsToPlaylist(playlist.id, *songList.toLongArray())
        }
        return playlistGateway.addSongsToPlaylist(playlist.id, *songList.toLongArray())
    }

}