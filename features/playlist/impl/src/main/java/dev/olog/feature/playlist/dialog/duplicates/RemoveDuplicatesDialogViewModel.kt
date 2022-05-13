package dev.olog.feature.playlist.dialog.duplicates

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.RemoveDuplicatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RemoveDuplicatesDialogViewModel @Inject constructor(
    private val useCase: RemoveDuplicatesUseCase
) : ViewModel() {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO){
        useCase(mediaId)
    }

}