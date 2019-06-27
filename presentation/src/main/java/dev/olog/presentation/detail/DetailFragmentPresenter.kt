package dev.olog.presentation.detail

import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.id
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.model.DisplayableItem
import dev.olog.core.entity.PlaylistType
import dev.olog.core.interactor.MoveItemInPlaylistUseCase
import dev.olog.core.interactor.RemoveFromPlaylistUseCase
import io.reactivex.Completable
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    fun removeFromPlaylist(item: DisplayableItem): Completable {
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        val playlistType = if (item.mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == AutoPlaylist.FAVORITE.id){
            // favorites use songId instead of idInPlaylist
            return removeFromPlaylistUseCase.execute(RemoveFromPlaylistUseCase.Input(
                    playlistId, item.mediaId.leaf!!, playlistType
            ))
        }
        return removeFromPlaylistUseCase.execute(RemoveFromPlaylistUseCase.Input(
                playlistId, item.trackNumber.toLong(), playlistType
        ))
    }

    fun moveInPlaylist(from: Int, to: Int){
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        moveItemInPlaylistUseCase.execute(MoveItemInPlaylistUseCase.Input(playlistId, from, to,
                if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        ))
    }

    fun showSortByTutorialIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.sortByTutorial()
    }

}