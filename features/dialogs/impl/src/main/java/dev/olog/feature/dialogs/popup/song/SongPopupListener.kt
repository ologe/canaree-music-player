package dev.olog.feature.dialogs.popup.song

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
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
import javax.inject.Inject

class SongPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val infoNavigator: FeatureInfoNavigator,
    private val detailNavigator: FeatureDetailNavigator,
    private val dialogsNavigator: FeatureDialogsNavigator,
    private val playlistNavigator: FeaturePlaylistNavigator,
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
            R.id.viewInfo -> viewInfo(activity, infoNavigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, detailNavigator, song.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, detailNavigator, song.getArtistMediaId())
            R.id.share -> share(activity, song)
            R.id.setRingtone -> setRingtone(activity, dialogsNavigator, getMediaId(), song)
        }


        return true
    }

    private fun toCreatePlaylist() {
        playlistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song.title)
    }

    private fun playLater() {
        dialogsNavigator.toPlayLater(activity, getMediaId(), -1, song.title)
    }

    private fun playNext() {
        dialogsNavigator.toPlayNext(activity, getMediaId(), -1, song.title)
    }

    private fun addToFavorite() {
        dialogsNavigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song.title)
    }

    private fun delete() {
        dialogsNavigator.toDeleteDialog(activity, getMediaId(), -1, song.title)
    }

}