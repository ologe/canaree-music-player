package dev.olog.domain.interactor

import dev.olog.domain.MediaId
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Track
import dev.olog.domain.MediaIdCategory.PLAYLISTS
import dev.olog.domain.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val trackGateway: TrackGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(mediaId: MediaId) {
        return when (mediaId) {
            is Track -> trackGateway.deleteSingle(mediaId.id.toLong())
            is Category -> handleCategory(mediaId)
        }
    }

    private suspend fun handleCategory(mediaId: Category) {
        return when (mediaId.category) {
            PODCASTS_PLAYLIST -> podcastPlaylistGateway.deletePlaylist(mediaId.categoryId.toLong())
            PLAYLISTS -> playlistGateway.deletePlaylist(mediaId.categoryId.toLong())
            else -> {
                val songList = getSongListByParamUseCase(mediaId)
                trackGateway.deleteGroup(songList.map { it.id })
            }
        }
    }

}