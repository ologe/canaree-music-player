package dev.olog.feature.dialog.playlist.duplicates

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.interactor.playlist.RemovePlaylistDuplicatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemovePlaylistDuplicatesDialogPresenter @Inject constructor(
    private val useCase: RemovePlaylistDuplicatesUseCase
) {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO){
        useCase(mediaId)
    }

}