package dev.olog.presentation.detail

import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.PlaylistType
import dev.olog.core.interactor.playlist.MoveItemInPlaylistUseCase
import dev.olog.core.interactor.playlist.RemoveFromPlaylistUseCase
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.detail.adapter.DetailItem
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    suspend fun removeFromPlaylist(
        parentMediaId: MediaId,
        mediaId: MediaId,
        idInPlaylist: Int
    ) {
        parentMediaId.assertPlaylist()
        val playlistId = parentMediaId.categoryId
        val playlistType = if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == AutoPlaylist.FAVORITE.id){
            // favorites use songId instead of idInPlaylist
            removeFromPlaylistUseCase(
                RemoveFromPlaylistUseCase.Input(
                    playlistId, mediaId.leaf!!, playlistType
            ))
        } else {
            removeFromPlaylistUseCase(
                RemoveFromPlaylistUseCase.Input(
                playlistId, idInPlaylist.toLong(), playlistType
            ))
        }
    }

    suspend fun moveInPlaylist(mediaId: MediaId, moveList: List<Pair<Int, Int>>){
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        moveItemInPlaylistUseCase.execute(
            MoveItemInPlaylistUseCase.Input(playlistId, moveList,
                if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        ))
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.sortByTutorial()
    }

}