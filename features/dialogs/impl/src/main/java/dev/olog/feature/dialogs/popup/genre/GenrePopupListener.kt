package dev.olog.feature.dialogs.popup.genre

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Genre
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
import javax.inject.Inject

class GenrePopupListener @Inject constructor(
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

    private lateinit var genre: Genre
    private var song: Song? = null

    fun setData(genre: Genre, song: Song?): GenrePopupListener {
        this.genre = genre
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null) {
            return MediaId.playableItem(genre.getMediaId(), song!!.id)
        } else {
            return genre.getMediaId()
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), genre.size, genre.name)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(activity, infoNavigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, detailNavigator, song!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, detailNavigator, song!!.getArtistMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(activity, dialogsNavigator, getMediaId(), song!!)
            R.id.addHomeScreen -> {
                appShortcuts.addDetailShortcut(getMediaId(), genre.name)
            }
        }

        return true
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            playlistNavigator.toCreatePlaylistDialog(activity, getMediaId(), genre.size, genre.name)
        } else {
            playlistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        mediaProvider.playFromMediaId(getMediaId(), null, null)
    }

    private fun playShuffle() {
        mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater() {
        if (song == null) {
            dialogsNavigator.toPlayLater(activity, getMediaId(), genre.size, genre.name)
        } else {
            dialogsNavigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            dialogsNavigator.toPlayNext(activity, getMediaId(), genre.size, genre.name)
        } else {
            dialogsNavigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            dialogsNavigator.toAddToFavoriteDialog(activity, getMediaId(), genre.size, genre.name)
        } else {
            dialogsNavigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            dialogsNavigator.toDeleteDialog(activity, getMediaId(), genre.size, genre.name)
        } else {
            dialogsNavigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

}