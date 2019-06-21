package dev.olog.msc.presentation.dialog.rename

import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.RenameUseCase
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class RenameDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    getPlaylistSiblingsUseCase: GetPlaylistsBlockingUseCase,
    private val renameUseCase: RenameUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase
            .execute(if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK)
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
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> !existingPlaylists.contains(playlistTitle.toLowerCase())
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

}