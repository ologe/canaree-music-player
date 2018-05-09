package dev.olog.msc.presentation.dialog.rename

import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.RenameUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import java.io.File
import javax.inject.Inject

class RenameDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        getPlaylistSiblingsUseCase: GetPlaylistsBlockingUseCase,
        private val renameUseCase: RenameUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase.execute()
            .map { it.title }
            .map { it.toLowerCase() }

    fun execute(newTitle: String) : Completable {
        return renameUseCase.execute(Pair(mediaId, newTitle))
    }

    /**
     * returns false if is invalid
     */
    fun checkData(playlistTitle: String): Boolean {
        return when {
            mediaId.isPlaylist -> !existingPlaylists.contains(playlistTitle.toLowerCase())
            mediaId.isFolder -> {
                val folderPath = mediaId.categoryValue
                val parent = File(folderPath).parent
                return !File(parent, playlistTitle).exists()
            }
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

}