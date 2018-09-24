package dev.olog.msc.presentation.dialog.create.playlist

import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.msc.domain.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        playlists: GetPlaylistsBlockingUseCase,
        private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) {

    private val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

    private val existingPlaylists = playlists.execute(playlistType)
            .map { it.title.toLowerCase() }

    fun execute(playlistTitle: String) : Completable {
        return insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle,
                listOf(mediaId.resolveId), playlistType))
    }

    fun isStringValid(string: String): Boolean {
        return !existingPlaylists.contains(string.toLowerCase())
    }

}