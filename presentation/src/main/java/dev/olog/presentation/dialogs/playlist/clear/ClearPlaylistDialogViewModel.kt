package dev.olog.presentation.dialogs.playlist.clear

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

@HiltViewModel
class ClearPlaylistDialogViewModel @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) {
        playlistGateway.clearPlaylist(mediaId.id)
    }

}