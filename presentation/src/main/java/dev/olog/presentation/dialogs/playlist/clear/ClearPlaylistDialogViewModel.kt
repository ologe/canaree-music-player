package dev.olog.presentation.dialogs.playlist.clear

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.ClearPlaylistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClearPlaylistDialogViewModel @Inject constructor(
    private val useCase: ClearPlaylistUseCase

) : ViewModel() {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        useCase(mediaId)
    }

}