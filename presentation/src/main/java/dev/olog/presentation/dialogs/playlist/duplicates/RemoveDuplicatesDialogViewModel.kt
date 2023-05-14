package dev.olog.presentation.dialogs.playlist.duplicates

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

@HiltViewModel
class RemoveDuplicatesDialogViewModel @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) {
        playlistGateway.removeDuplicated(mediaId.id)
    }

}