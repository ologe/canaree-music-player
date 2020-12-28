package dev.olog.presentation.popup.playlist

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.lib.media.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import dev.olog.shared.android.extensions.toast
import javax.inject.Inject

class PlaylistPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val appShortcuts: AppShortcuts,
    private val navigator: NavigatorLegacy,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var playlist: Playlist
    private var track: Track? = null

    fun setData(playlist: Playlist, track: Track?): PlaylistPopupListener {
        this.playlist = playlist
        this.track = track
        return this
    }

    private fun getMediaId(): MediaId {
        if (track != null) {
            val playlistMediaId = playlist.getMediaId()
            return MediaId.playableItem(playlistMediaId, track!!.id)
        } else {
            return playlist.getMediaId()
        }
    }

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
            R.id.delete -> delete()
            R.id.rename -> rename()
            R.id.clear -> clearPlaylist()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, track!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(navigator, track!!.getArtistMediaId())
            R.id.share -> share(activity, track!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), track!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), playlist.title)
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        navigator.toRemoveDuplicatesDialog(playlist.getMediaId(), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (track == null) {
            navigator.toCreatePlaylistDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId(), -1, track!!.title)
        }
    }

    private fun playFromMediaId() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            activity.mediaProvider.playFromMediaId(getMediaId(), null, null)
        }
    }

    private fun playShuffle() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            activity.mediaProvider.shuffle(getMediaId(), null)
        }
    }

    private fun playLater() {
        if (track == null) {
            navigator.toPlayLater(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayLater(getMediaId(), -1, track!!.title)
        }
    }

    private fun playNext() {
        if (track == null) {
            navigator.toPlayNext(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayNext(getMediaId(), -1, track!!.title)
        }
    }


    private fun addToFavorite() {
        if (track == null) {
            navigator.toAddToFavoriteDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, track!!.title)
        }
    }

    private fun delete() {
        if (track == null) {
            navigator.toDeleteDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, track!!.title)
        }
    }

    private fun rename() {
        navigator.toRenameDialog(getMediaId(), playlist.title)
    }

    private fun clearPlaylist() {
        navigator.toClearPlaylistDialog(getMediaId(), playlist.title)
    }


}