package dev.olog.feature.dialogs.popup.playlist

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.dialogs.R
import dev.olog.feature.dialogs.popup.AbsPopup
import dev.olog.feature.dialogs.popup.AbsPopupListener
import dev.olog.feature.edit.FeatureInfoNavigator
import dev.olog.feature.playlist.FeaturePlaylistNavigator
import dev.olog.media.MediaProvider
import dev.olog.shared.android.extensions.toast
import javax.inject.Inject

class PlaylistPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val infoNavigator: FeatureInfoNavigator,
    private val detailNavigator: FeatureDetailNavigator,
    private val dialogsNavigator: FeatureDialogsNavigator,
    private val playlistNavigator: FeaturePlaylistNavigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val appShortcuts: AppShortcuts,
) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var playlist: Playlist
    private var song: Song? = null

    fun setData(playlist: Playlist, song: Song?): PlaylistPopupListener {
        this.playlist = playlist
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null) {
            val playlistMediaId = playlist.getMediaId()
            return MediaId.playableItem(playlistMediaId, song!!.id)
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
            R.id.viewInfo -> viewInfo(activity, infoNavigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, detailNavigator, song!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, detailNavigator, song!!.getArtistMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(activity, dialogsNavigator, getMediaId(), song!!)
            R.id.addHomeScreen -> {
                appShortcuts.addDetailShortcut(getMediaId(), playlist.title)
            }
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        playlistNavigator.toRemoveDuplicatesDialog(activity, playlist.getMediaId(), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            playlistNavigator.toCreatePlaylistDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            playlistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        if (playlist.size == 0) {
            activity.toast(localization.R.string.common_empty_list)
        } else {
            mediaProvider.playFromMediaId(getMediaId(), null, null)
        }
    }

    private fun playShuffle() {
        if (playlist.size == 0) {
            activity.toast(localization.R.string.common_empty_list)
        } else {
            mediaProvider.shuffle(getMediaId(), null)
        }
    }

    private fun playLater() {
        if (song == null) {
            dialogsNavigator.toPlayLater(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            dialogsNavigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            dialogsNavigator.toPlayNext(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            dialogsNavigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            dialogsNavigator.toAddToFavoriteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            dialogsNavigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            dialogsNavigator.toDeleteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            dialogsNavigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun rename() {
        playlistNavigator.toRenameDialog(activity, getMediaId(), playlist.title)
    }

    private fun clearPlaylist() {
        playlistNavigator.toClearPlaylistDialog(activity, getMediaId(), playlist.title)
    }

}