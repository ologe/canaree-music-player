package dev.olog.feature.playlist.dialog.rename

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.RenameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RenameDialogViewModel @Inject constructor(
    private val renameUseCase: RenameUseCase
) : ViewModel() {


    suspend fun execute(mediaId: MediaId, newTitle: String) = withContext(Dispatchers.IO){
        renameUseCase(mediaId, newTitle)
    }

}