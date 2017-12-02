package dev.olog.presentation.dialog_add_playlist

import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetActualPlaylistUseCase
import dev.olog.shared.MediaIdHelper
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddPlaylistPresenter @Inject constructor(
        private val mediaId: String,
        private val getPlaylistSiblingsUseCase: GetActualPlaylistUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) {

    fun getPlaylistsAsList(): List<DisplayablePlaylist> {
        return getPlaylistSiblingsUseCase.execute()
                .map { DisplayablePlaylist(it.id, "- ${it.title}") }
    }

    fun onItemClick(position: Int): Single<Pair<String, String>> {

        if (MediaIdHelper.extractCategory(mediaId) == MediaIdHelper.MEDIA_ID_BY_ALL){
            return getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .map { Pair(it, getPlaylistsAsList()[position]) }
                    .flatMap { (song, playlist) -> addToPlaylistUseCase
                            .execute(Pair(playlist.playlistId, mediaId))
                            .map { (song.title).to(playlist.playlistTitle) }
                    }
        }

        return getSongListByParamUseCase.execute(mediaId)
                .observeOn(Schedulers.computation())
                .firstOrError()
                .map { Pair(it, getPlaylistsAsList()[position]) }
                .flatMap { (songList, playlist) -> addToPlaylistUseCase
                        .execute(Pair(playlist.playlistId, mediaId))
                        .map { (songList.size.toString()).to(playlist.playlistTitle) }
                }
    }

}

data class DisplayablePlaylist(
        val playlistId: Long,
        val playlistTitle: String
)