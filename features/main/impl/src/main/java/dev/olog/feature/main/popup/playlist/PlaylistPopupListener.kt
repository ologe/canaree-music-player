package dev.olog.feature.main.popup.playlist

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.edit.api.FeatureEditNavigator
import dev.olog.feature.main.R
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.popup.AbsPopup
import dev.olog.feature.main.popup.AbsPopupListener
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import dev.olog.feature.shortcuts.api.AppShortcuts
import dev.olog.shared.extension.toast
import javax.inject.Inject

class PlaylistPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featurePlaylistNavigator: FeaturePlaylistNavigator,
    private val featureDetailNavigator: FeatureDetailNavigator,
    private val featureEditNavigator: FeatureEditNavigator,
    private val featureMainNavigator: FeatureMainNavigator,
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

        onPlaylistSubItemClick(activity, itemId.toString(), getMediaId(), playlist.size, playlist.title)

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
            R.id.viewInfo -> viewInfo(activity, featureEditNavigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, featureDetailNavigator, song!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, featureDetailNavigator, song!!.getArtistMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(activity, featureMainNavigator, getMediaId(), song!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(
                mediaId = getMediaId(),
                title = playlist.title
            )
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        featurePlaylistNavigator.toRemoveDuplicatesDialog(activity, playlist.getMediaId(), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        if (playlist.size == 0) {
            activity.toast(localization.R.string.common_empty_list)
        } else {
            mediaProvider.playFromMediaId(getMediaId(), null)
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
            featureMainNavigator.toPlayLater(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            featureMainNavigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            featureMainNavigator.toPlayNext(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            featureMainNavigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            featureMainNavigator.toAddToFavoriteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            featureMainNavigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            featureMainNavigator.toDeleteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            featureMainNavigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun rename() {
        featurePlaylistNavigator.toRenameDialog(activity, getMediaId(), playlist.title)
    }

    private fun clearPlaylist() {
        featurePlaylistNavigator.toClearPlaylistDialog(activity, getMediaId(), playlist.title)
    }


}