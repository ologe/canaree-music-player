package dev.olog.domain.interactor.playlist

import dev.olog.domain.MediaId
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Track
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
        sanitize(playlist, mediaId)
        return when (mediaId) {
            is Track -> handleTracks(playlist, mediaId)
            is Category -> handleCategories(playlist, mediaId)
        }

    }

    private suspend fun handleTracks(playlist: Playlist, mediaId: Track) {
        if (mediaId.isAnyPodcast) {
            podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.id.toLong()))
        } else {
            playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.id.toLong()))
        }
    }

    private suspend fun handleCategories(playlist: Playlist, mediaId: Category) {
        val songList = getSongListByParamUseCase(mediaId).map { it.id }
        if (mediaId.isAnyPodcast) {
            podcastPlaylistGateway.addSongsToPlaylist(playlist.id, songList)
        } else {
            playlistGateway.addSongsToPlaylist(playlist.id, songList)
        }
    }

    private fun sanitize(playlist: Playlist, mediaId: MediaId) {
        require(playlist.isPodcast == mediaId.isAnyPodcast) {
            throw IllegalArgumentException("playlist is ${playlist.isPodcast} but media id is ${mediaId.isAnyPodcast}")
        }
    }

}