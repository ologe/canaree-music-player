package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Track
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
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