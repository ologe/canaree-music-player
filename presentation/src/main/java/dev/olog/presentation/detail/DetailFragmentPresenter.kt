package dev.olog.presentation.detail

import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.interactor.playlist.MoveItemInPlaylistUseCase
import dev.olog.domain.interactor.playlist.RemoveFromPlaylistUseCase
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory.PLAYLISTS
import dev.olog.feature.presentation.base.model.PresentationIdCategory.PODCASTS_PLAYLIST
import dev.olog.feature.presentation.base.model.DisplayableTrack
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
    private val mediaId: PresentationId.Category,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    suspend fun removeFromPlaylist(item: DisplayableTrack) {
        require(mediaId.category == PLAYLISTS || mediaId.category == PODCASTS_PLAYLIST)

        val playlistId = mediaId.categoryId.toLong()

        val playlistType = playlistType()
        if (playlistId == AutoPlaylist.FAVORITE.id) {
            // favorites use songId instead of idInPlaylist
            removeFromPlaylistUseCase(
                RemoveFromPlaylistUseCase.Input(
                    playlistId, item.mediaId.id.toLong(), playlistType
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

        val playlistId = mediaId.categoryId.toLong()
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