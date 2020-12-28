package dev.olog.presentation.popup.folder

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.lib.media.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class FolderPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val appShortcuts: AppShortcuts,
    private val navigator: NavigatorLegacy,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var folder: Folder
    private var track: Track? = null

    fun setData(folder: Folder, track: Track?): FolderPopupListener {
        this.folder = folder
        this.track = track
        return this
    }

    private fun getMediaId(): MediaId {
        if (track != null) {
            val folderMediaId = folder.getMediaId()
            return MediaId.playableItem(folderMediaId, track!!.id)
        } else {
            return folder.getMediaId()
        }
    }

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
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, track!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(navigator, track!!.getArtistMediaId())
            R.id.share -> share(activity, track!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), track!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), folder.title)
        }


        return true
    }

    private fun toCreatePlaylist() {
        if (track == null) {
            navigator.toCreatePlaylistDialog(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId(), -1, track!!.title)
        }
    }

    private fun playFromMediaId() {
        activity.mediaProvider.playFromMediaId(getMediaId(), null, null)
    }

    private fun playShuffle() {
        activity.mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater() {
        if (track == null) {
            navigator.toPlayLater(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toPlayLater(getMediaId(), -1, track!!.title)
        }
    }

    private fun playNext() {
        if (track == null) {
            navigator.toPlayNext(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toPlayNext(getMediaId(), -1, track!!.title)
        }
    }


    private fun addToFavorite() {
        if (track == null) {
            navigator.toAddToFavoriteDialog(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, track!!.title)
        }
    }

    private fun delete() {
        if (track == null) {
            navigator.toDeleteDialog(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, track!!.title)
        }
    }

}