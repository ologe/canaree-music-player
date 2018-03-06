package dev.olog.msc.presentation.dialog.create.playlist

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.CreatePlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        playlists: GetPlaylistBlockingUseCase,
        private val createPlaylistUseCase: CreatePlaylistUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) {

    private val existingPlaylists = playlists.execute()
            .map { it.title.toLowerCase() }

    fun execute(playlistTitle: String) : Completable {

        return createPlaylistUseCase.execute(playlistTitle)
                .map { Playlist(it, playlistTitle, -1, "") }
                .flatMapCompletable { addToPlaylistUseCase.execute(it to mediaId) }
    }

    fun isStringValid(string: String): Boolean {
        return !existingPlaylists.contains(string.toLowerCase())
    }

}