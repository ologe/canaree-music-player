package dev.olog.presentation.detail

import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.PlaylistType
import dev.olog.core.interactor.playlist.MoveItemInPlaylistUseCase
import dev.olog.core.interactor.playlist.RemoveFromPlaylistUseCase
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.PresentationId
import dev.olog.presentation.PresentationIdCategory.PLAYLISTS
import dev.olog.presentation.PresentationIdCategory.PODCASTS_PLAYLIST
import dev.olog.presentation.model.DisplayableTrack
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
    private val mediaId: PresentationId.Category,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    suspend fun removeFromPlaylist(item: DisplayableTrack) {
        require(mediaId.category == PLAYLISTS || mediaId.category == PODCASTS_PLAYLIST)

        val playlistId = mediaId.categoryId

        val playlistType = playlistType()
        if (playlistId == AutoPlaylist.FAVORITE.id) {
            // favorites use songId instead of idInPlaylist
            removeFromPlaylistUseCase(
                RemoveFromPlaylistUseCase.Input(
                    playlistId, item.mediaId.id, playlistType
                )
            )
        } else {
            removeFromPlaylistUseCase(
                RemoveFromPlaylistUseCase.Input(
                    playlistId, item.idInPlaylist.toLong(), playlistType
                )
            )
        }
    }

    suspend fun moveInPlaylist(moveList: List<Pair<Int, Int>>) {
        require(mediaId.category == PLAYLISTS || mediaId.category == PODCASTS_PLAYLIST)

        val playlistId = mediaId.categoryId
        moveItemInPlaylistUseCase(
            MoveItemInPlaylistUseCase.Input(playlistId, moveList, playlistType())
        )
    }

    private fun playlistType(): PlaylistType {
        return if (mediaId.category == PODCASTS_PLAYLIST) {
            PlaylistType.PODCAST
        } else {
            PlaylistType.TRACK
        }
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.sortByTutorial()
    }

}