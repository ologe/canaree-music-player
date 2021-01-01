package dev.olog.feature.dialog.playlist.create

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.schedulers.Schedulers
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreatePlaylistDialogViewModel @Inject constructor(
    @Assisted private val state: SavedStateHandle,
    private val schedulers: Schedulers,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val playingQueueGateway: PlayingQueueGateway,
    private val podcastGateway: PodcastGateway,
    private val songGateway: SongGateway

) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)

    suspend fun execute(
        playlistTitle: String
    ) = withContext(schedulers.cpu) {

        val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

        val trackToInsert = when (mediaId) {
            is MediaId.Category -> getSongListByParamUseCase(mediaId).map(Track::id)
            is MediaId.Track -> {
                if (mediaId.isAnyPodcast) {
                    listOf(podcastGateway.getByParam(mediaId.id)!!.id)
                } else {
                    listOf(songGateway.getByParam(mediaId.id)!!.id)
                }
            }
//            mediaId.isPlayingQueue -> playingQueueGateway.getAll().map { it.track.id } TODO
        }
        insertCustomTrackListToPlaylist(
            playlistTitle = playlistTitle,
            type = playlistType,
            *trackToInsert.toLongArray(),
        )
    }

}