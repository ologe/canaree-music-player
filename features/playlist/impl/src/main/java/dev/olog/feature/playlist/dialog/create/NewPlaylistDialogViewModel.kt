package dev.olog.feature.playlist.dialog.create

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.core.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewPlaylistDialogViewModel @Inject constructor(
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val playingQueueGateway: PlayingQueueGateway,
    private val podcastGateway: PodcastGateway,
    private val songGateway: SongGateway

) : ViewModel() {

    suspend fun execute(mediaId: MediaId, playlistTitle: String) = withContext(Dispatchers.IO) {
        val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

        val trackToInsert = when {
            mediaId.isPlayingQueue -> playingQueueGateway.getAll().map { it.song.id }
            mediaId.isLeaf && mediaId.isPodcast -> listOf(podcastGateway.getByParam(mediaId.resolveId)!!.id)
            mediaId.isLeaf -> listOf(songGateway.getByParam(mediaId.resolveId)!!.id)
            else -> getSongListByParamUseCase(mediaId).map { it.id }
        }
        insertCustomTrackListToPlaylist(InsertCustomTrackListRequest(playlistTitle, trackToInsert, playlistType))
    }

}