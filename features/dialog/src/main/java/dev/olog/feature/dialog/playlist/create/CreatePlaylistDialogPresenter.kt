package dev.olog.feature.dialog.playlist.create

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.domain.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreatePlaylistDialogPresenter @Inject constructor(
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val playingQueueGateway: PlayingQueueGateway,
    private val podcastGateway: PodcastGateway,
    private val songGateway: SongGateway

) {

    suspend fun execute(mediaId: MediaId, playlistTitle: String) = withContext(Dispatchers.IO) {
        val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

        val trackToInsert = when {
//            mediaId.isPlayingQueue -> playingQueueGateway.getAll().map { it.track.id } TODO
            mediaId.isLeaf && mediaId.isPodcast -> listOf(podcastGateway.getByParam(mediaId.resolveId)!!.id)
            mediaId.isLeaf -> listOf(songGateway.getByParam(mediaId.resolveId)!!.id)
            else -> getSongListByParamUseCase(mediaId).map { it.id }
        }
        insertCustomTrackListToPlaylist(InsertCustomTrackListRequest(playlistTitle, trackToInsert, playlistType))
    }

}