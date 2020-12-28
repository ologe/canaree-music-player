package dev.olog.presentation.popup.artist

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.lib.media.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class ArtistPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val appShortcuts: AppShortcuts,
    private val navigator: NavigatorLegacy,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var artist: Artist
    private var track: Track? = null

    fun setData(artist: Artist, track: Track?): ArtistPopupListener {
        this.artist = artist
        this.track = track
        return this
    }

    private fun getMediaId(): MediaId {
        if (track != null) {
            return MediaId.playableItem(artist.getMediaId(), track!!.id)
        } else {
            return artist.getMediaId()
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), artist.songs, artist.name)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, track!!.getArtistMediaId())
            R.id.viewArtist -> viewArtist(navigator, artist.getMediaId())
            R.id.share -> share(activity, track!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), track!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), artist.name)
        }


        return true
    }

    private fun toCreatePlaylist() {
        if (track == null) {
            navigator.toCreatePlaylistDialog(getMediaId(), artist.songs, artist.name)
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
            navigator.toPlayLater(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toPlayLater(getMediaId(), -1, track!!.title)
        }
    }

    private fun playNext() {
        if (track == null) {
            navigator.toPlayNext(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toPlayNext(getMediaId(), -1, track!!.title)
        }
    }


    private fun addToFavorite() {
        if (track == null) {
            navigator.toAddToFavoriteDialog(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, track!!.title)
        }
    }

    private fun delete() {
        if (track == null) {
            navigator.toDeleteDialog(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, track!!.title)
        }
    }

}