package dev.olog.domain.interactor

import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.shared.exhaustive
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val songGateway: SongGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId) {
            is MediaId.Track -> handleTrack(mediaId)
            is MediaId.Category -> handleCategory(mediaId)
        }.exhaustive
    }


    private suspend fun handleTrack(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return podcastGateway.deleteSingle(mediaId.id)
        }
        return songGateway.deleteSingle(mediaId.id)
    }

    private suspend fun handleCategory(mediaId: MediaId.Category) {
        if (mediaId.isPodcastPlaylist) {
            return podcastPlaylistGateway.deletePlaylist(mediaId.categoryValue.toLong())
        }
        if (mediaId.isPlaylist) {
            return playlistGateway.deletePlaylist(mediaId.categoryValue.toLong())
        }

        val songList = getSongListByParamUseCase(mediaId)
        if (mediaId.isAnyPodcast){
            podcastGateway.deleteGroup(songList)
        } else {
            songGateway.deleteGroup(songList)
        }
    }
}