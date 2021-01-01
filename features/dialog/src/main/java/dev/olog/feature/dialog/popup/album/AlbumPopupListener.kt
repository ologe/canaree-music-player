package dev.olog.feature.dialog.popup.album

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.domain.AppShortcuts
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.Track
import dev.olog.domain.interactor.playlist.AddToPlaylistUseCase
import dev.olog.domain.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.dialog.R
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.navigation.internal.ActivityProvider
import dev.olog.feature.dialog.popup.AbsPopupListener
import javax.inject.Inject

class AlbumPopupListener @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val appShortcuts: AppShortcuts,
    private val navigator: Navigator,
    getPlaylistsUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase
) : AbsPopupListener(
    getPlaylistUseCase = getPlaylistsUseCase,
    addToPlaylistUseCase = addToPlaylistUseCase,
    podcastPlaylist = false
) {

    private val activity: FragmentActivity
        get() = activityProvider()!!

    private lateinit var album: Album
    private var track: Track? = null

    fun setData(album: Album, track: Track?): AlbumPopupListener {
        this.album = album
        this.track = track
        return this
    }

    private fun getMediaId(): MediaId {
        if (track != null) {
            return MediaId.playableItem(album.getMediaId(), track!!.id)
        } else {
            return album.getMediaId()
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), album.songs, album.title)

        when (itemId) {
            R.id.newPlaylist -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewArtist -> viewArtist()
            R.id.viewAlbum -> viewAlbum(navigator, album.getMediaId())
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.share -> share(activity, track!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), track!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), album.title)
        }

        return true
    }

    private fun toCreatePlaylist() {
        if (track == null) {
            navigator.toCreatePlaylist(getMediaId(), album.songs, album.title)
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
            navigator.toPlayLater(getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayLater(getMediaId(), -1, track!!.title)
        }
    }

    private fun playNext() {
        if (track == null) {
            navigator.toPlayNext(getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayNext(getMediaId(), -1, track!!.title)
        }
    }


    private fun addToFavorite() {
        if (track == null) {
            navigator.toAddToFavorite(getMediaId(), album.songs, album.title)
        } else {
            navigator.toAddToFavorite(getMediaId(), -1, track!!.title)
        }
    }

    private fun delete() {
        if (track == null) {
            navigator.toDelete(getMediaId(), album.songs, album.title)
        } else {
            navigator.toDelete(getMediaId(), -1, track!!.title)
        }
    }

    private fun viewArtist() {
        navigator.toDetailFragment(album.getArtistMediaId())
    }


}