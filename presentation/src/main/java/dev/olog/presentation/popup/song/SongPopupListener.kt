package dev.olog.presentation.popup.song

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class SongPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val navigator: NavigatorLegacy,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var song: Song

    fun setData(song: Song): SongPopupListener {
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        return song.getMediaId()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), -1, song.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, song.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(navigator, song.getArtistMediaId())
            R.id.share -> share(activity, song)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song)
        }


        return true
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylistDialog(getMediaId(), -1, song.title)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), -1, song.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), -1, song.title)
    }

    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(getMediaId(), -1, song.title)
    }

    private fun delete() {
        navigator.toDeleteDialog(getMediaId(), -1, song.title)
    }

}