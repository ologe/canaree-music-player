package dev.olog.presentation.popup.folder

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.shortcuts.api.FeatureShortcutsNavigator
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.playlist.create.NewPlaylistDialog.NavArgs.FromMediaId
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class FolderPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    playlistGateway: PlaylistGateway,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featureShortcutsNavigator: FeatureShortcutsNavigator,
) : AbsPopupListener(playlistGateway, addToPlaylistUseCase) {

    private lateinit var folder: Folder

    fun setData(folder: Folder): FolderPopupListener {
        this.folder = folder
        return this
    }

    private fun getMediaId(): MediaId = folder.getMediaId()

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), folder.size, folder.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.addHomeScreen -> featureShortcutsNavigator.addDetailShortcut(
                mediaId = getMediaId(),
                title = folder.title
            )
        }


        return true
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylistDialog(FromMediaId(getMediaId(), folder.title))
    }

    private fun playFromMediaId() {
        mediaProvider.playFromMediaId(getMediaId(), null, null)
    }

    private fun playShuffle() {
        mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), folder.size, folder.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), folder.size, folder.title)
    }

    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(getMediaId(), folder.size, folder.title)
    }

}