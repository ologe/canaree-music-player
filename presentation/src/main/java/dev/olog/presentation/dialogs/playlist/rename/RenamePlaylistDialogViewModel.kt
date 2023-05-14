package dev.olog.presentation.dialogs.playlist.rename

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.gateway.QueryMode
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

@HiltViewModel
class RenamePlaylistDialogViewModel @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) : ViewModel() {

    fun getPlaylistTitles(): Collection<String> {
        return playlistGateway.getAll(QueryMode.All).map { it.title }
    }

    suspend fun execute(mediaId: MediaId, newTitle: String) {
        playlistGateway.renamePlaylist(mediaId.id, newTitle)
    }

}