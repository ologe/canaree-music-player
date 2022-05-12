package dev.olog.presentation.popup.genre

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.media.MediaProvider
import dev.olog.feature.playlist.FeaturePlaylistNavigator
import dev.olog.feature.shortcuts.AppShortcuts
import dev.olog.presentation.R
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

class GenrePopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featurePlaylistNavigator: FeaturePlaylistNavigator,
    private val featureDetailNavigator: FeatureDetailNavigator,
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
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, featureDetailNavigator, song!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, featureDetailNavigator, song!!.getArtistMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(
                mediaId = getMediaId(),
                title = genre.name
            )
        }

        return true
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), genre.size, genre.name)
        } else {
            featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
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
            navigator.toPlayLater(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toPlayLater(getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            navigator.toPlayNext(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toPlayNext(getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            navigator.toAddToFavoriteDialog(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            navigator.toDeleteDialog(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, song!!.title)
        }
    }

}