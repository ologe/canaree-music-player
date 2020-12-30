package dev.olog.feature.dialog.playlist.rename

import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.RenameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RenamePlaylistPresenter @Inject constructor(
    private val renameUseCase: RenameUseCase
) {


    suspend fun execute(mediaId: MediaId, newTitle: String) = withContext(Dispatchers.IO){
        renameUseCase(mediaId, newTitle)
    }

}