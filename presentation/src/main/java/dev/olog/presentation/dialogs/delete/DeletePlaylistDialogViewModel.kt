package dev.olog.presentation.dialogs.delete

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

@HiltViewModel
class DeletePlaylistDialogViewModel @Inject constructor(
    private val gateway: PlaylistGateway,
) : ViewModel() {

    suspend fun execute(mediaId: MediaId)  {
        gateway.deletePlaylist(mediaId.id)
    }

}