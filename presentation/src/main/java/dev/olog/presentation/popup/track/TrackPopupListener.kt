package dev.olog.presentation.popup.track

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class TrackPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val navigator: NavigatorLegacy,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var track: Track

    fun setData(track: Track): TrackPopupListener {
        this.track = track
        return this
    }

    private fun getMediaId(): MediaId {
        return track.getMediaId()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), -1, track.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, track.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(navigator, track.getArtistMediaId())
            R.id.share -> share(activity, track)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), track)
        }


        return true
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylistDialog(getMediaId(), -1, track.title)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), -1, track.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), -1, track.title)
    }

    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(getMediaId(), -1, track.title)
    }

    private fun delete() {
        navigator.toDeleteDialog(getMediaId(), -1, track.title)
    }

}