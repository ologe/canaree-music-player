package dev.olog.presentation.dialogs.playlist.create

import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.core.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.toDomain
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val playingQueueGateway: PlayingQueueGateway,
    private val trackGateway: TrackGateway,
    private val schedulers: Schedulers

) {

    suspend fun execute(
        mediaId: PresentationId,
        playlistTitle: String
    ) = withContext(schedulers.io) {
        val playlistType = if (mediaId.isAnyPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

        val trackToInsert = when (mediaId) {
            is PresentationId.Track -> listOf(trackGateway.getByParam(mediaId.id)!!.id)
            is PresentationId.Category -> getSongListByParamUseCase(mediaId.toDomain()).map { it.id }
        }
        insertCustomTrackListToPlaylist(InsertCustomTrackListToPlaylist.Input(playlistTitle, trackToInsert, playlistType))
    }

    suspend fun savePlayingQueue(
        playlistTitle: String
    ) = withContext(schedulers.io) {
        val playlistType = PlaylistType.TRACK

        val trackToInsert = playingQueueGateway.getAll().map { it.song.id }
        insertCustomTrackListToPlaylist(InsertCustomTrackListToPlaylist.Input(playlistTitle, trackToInsert, playlistType))
    }

}