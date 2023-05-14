package dev.olog.presentation.popup.playlist

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.AutoPlaylist
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.shortcuts.api.FeatureShortcutsNavigator
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.playlist.create.NewPlaylistDialog.NavArgs.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class AutoPlaylistPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    playlistGateway: PlaylistGateway,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featureShortcutsNavigator: FeatureShortcutsNavigator,
) : AbsPopupListener(playlistGateway, addToPlaylistUseCase) {

    private lateinit var playlist: AutoPlaylist

    fun setData(playlist: AutoPlaylist): AutoPlaylistPopupListener {
        this.playlist = playlist
        return this
    }

    private fun getMediaId(): MediaId = playlist.getMediaId()

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), playlist.size, playlist.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.clear -> clearPlaylist()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.addHomeScreen -> featureShortcutsNavigator.addDetailShortcut(
                mediaId = getMediaId(),
                title = playlist.title
            )
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        navigator.toRemoveDuplicatesDialog(playlist.getMediaId(), playlist.title)
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylistDialog(FromMediaId(getMediaId(), playlist.title))
    }

    private fun playFromMediaId() {
        mediaProvider.playFromMediaId(getMediaId(), null, null)
    }

    private fun playShuffle() {
        mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), playlist.size, playlist.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), playlist.size, playlist.title)
    }


    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(getMediaId(), playlist.size, playlist.title)
    }

    private fun clearPlaylist() {
        navigator.toClearPlaylistDialog(getMediaId(), playlist.title)
    }


}