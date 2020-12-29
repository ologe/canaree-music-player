package dev.olog.presentation.popup.track

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Track
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.navigation.Navigator
import dev.olog.navigation.internal.ActivityProvider
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class TrackPopupListener @Inject constructor(
    private val activityProvider: ActivityProvider,
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

    private lateinit var track: Track

    fun setData(track: Track): TrackPopupListener {
        this.track = track
        return this
    }

    private fun getMediaId(): MediaId {
        return track.getMediaId()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), -1, track.title)

        when (itemId) {
            R.id.newPlaylist -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, track.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(navigator, track.getArtistMediaId())
            R.id.share -> share(activity, track)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), track)
        }


        return true
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylist(getMediaId(), -1, track.title)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), -1, track.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), -1, track.title)
    }

    private fun addToFavorite() {
        navigator.toAddToFavorite(getMediaId(), -1, track.title)
    }

    private fun delete() {
        navigator.toDelete(getMediaId(), -1, track.title)
    }

}