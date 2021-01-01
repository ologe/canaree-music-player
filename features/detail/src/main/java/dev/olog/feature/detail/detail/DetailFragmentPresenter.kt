package dev.olog.feature.detail.detail

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.interactor.playlist.MoveItemInPlaylistUseCase
import dev.olog.domain.interactor.playlist.RemoveFromPlaylistUseCase
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.feature.detail.detail.model.DetailFragmentModel
import javax.inject.Inject

internal class DetailFragmentPresenter @Inject constructor(
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    // TODO refactor, use proper type system
    suspend fun removeFromPlaylist(
        item: DetailFragmentModel.PlaylistTrack
    ) {

        val playlistId = item.mediaId.categoryValue.toLong()
        val playlistType = if (item.mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == AutoPlaylist.FAVORITE.id){
            // favorites use songId instead of idInPlaylist
            removeFromPlaylistUseCase(
                playlistId = playlistId,
                type = playlistType,
                idInPlaylist = item.mediaId.id
            )
        } else {
            removeFromPlaylistUseCase(
                playlistId = playlistId,
                type = playlistType,
                idInPlaylist = item.idInPlaylist
            )
        }
    }

    suspend fun moveInPlaylist(
        parentMediaId: MediaId,
        moveList: List<Pair<Int, Int>>
    ){
        require(parentMediaId.isAnyPlaylist)
        val playlistId = parentMediaId.categoryValue.toLong()
        val type = if (parentMediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        moveItemInPlaylistUseCase.execute(
            playlistId = playlistId,
            type = type,
            moveList = moveList,
        )
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.sortByTutorial()
    }

}