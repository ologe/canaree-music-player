package dev.olog.presentation.popup.album

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
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

class AlbumPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    playlistGateway: PlaylistGateway,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featureShortcutsNavigator: FeatureShortcutsNavigator,
) : AbsPopupListener(playlistGateway, addToPlaylistUseCase) {

    private lateinit var album: Album

    fun setData(album: Album): AlbumPopupListener {
        this.album = album
        return this
    }

    private fun getMediaId(): MediaId = album.getMediaId()

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), album.size, album.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.viewArtist -> viewArtist()
            R.id.viewAlbum -> viewAlbum(navigator, album.getMediaId())
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.addHomeScreen -> featureShortcutsNavigator.addDetailShortcut(
                mediaId = getMediaId(),
                title = album.title
            )
        }

        return true
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylistDialog(FromMediaId(getMediaId(), album.title))
    }

    private fun playFromMediaId() {
        mediaProvider.playFromMediaId(getMediaId(), null, null)
    }

    private fun playShuffle() {
        mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), album.size, album.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), album.size, album.title)
    }


    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(getMediaId(), album.size, album.title)
    }

    private fun viewArtist() {
        navigator.toDetailFragment(album.getArtistMediaId())
    }


}