package dev.olog.feature.dialog.popup.playlist

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.AppShortcuts
import dev.olog.core.mediaid.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.dialog.R
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.navigation.internal.ActivityProvider
import dev.olog.feature.dialog.popup.AbsPopupListener
import dev.olog.shared.android.extensions.toast
import javax.inject.Inject

class PlaylistPopupListener @Inject constructor(
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
            R.id.newPlaylist -> toCreatePlaylist()
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
        navigator.toRemoveDuplicates(playlist.getMediaId(), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (track == null) {
            navigator.toCreatePlaylist(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylist(getMediaId(), -1, track!!.title)
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
            navigator.toAddToFavorite(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toAddToFavorite(getMediaId(), -1, track!!.title)
        }
    }

    private fun delete() {
        if (track == null) {
            navigator.toDelete(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toDelete(getMediaId(), -1, track!!.title)
        }
    }

    private fun rename() {
        navigator.toRename(getMediaId(), playlist.title)
    }

    private fun clearPlaylist() {
        navigator.toClearPlaylist(getMediaId(), playlist.title)
    }


}