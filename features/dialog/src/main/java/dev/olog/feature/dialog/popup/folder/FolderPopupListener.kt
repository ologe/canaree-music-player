package dev.olog.feature.dialog.popup.folder

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.AppShortcuts
import dev.olog.core.mediaid.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.dialog.R
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.navigation.internal.ActivityProvider
import dev.olog.feature.dialog.popup.AbsPopupListener
import javax.inject.Inject

class FolderPopupListener @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val appShortcuts: AppShortcuts,
    private val navigator: Navigator,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase
) : AbsPopupListener(
    getPlaylistUseCase = getPlaylistBlockingUseCase,
    addToPlaylistUseCase = addToPlaylistUseCase,
    podcastPlaylist = false
) {

    private val activity: FragmentActivity
        get() = activityProvider()!!

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
            R.id.newPlaylist -> toCreatePlaylist()
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
            navigator.toCreatePlaylist(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toCreatePlaylist(getMediaId(), -1, track!!.title)
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
            navigator.toAddToFavorite(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toAddToFavorite(getMediaId(), -1, track!!.title)
        }
    }

    private fun delete() {
        if (track == null) {
            navigator.toDelete(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toDelete(getMediaId(), -1, track!!.title)
        }
    }

}