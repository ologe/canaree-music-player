package dev.olog.presentation.popup.genre

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.feature.app.shortcuts.AppShortcuts
import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Genre
import dev.olog.domain.entity.track.Song
import dev.olog.domain.interactor.playlist.AddToPlaylistUseCase
import dev.olog.domain.interactor.playlist.GetPlaylistsUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.*
import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.*
import dev.olog.navigation.Navigator
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import java.lang.ref.WeakReference
import javax.inject.Inject

internal class GenrePopupListener @Inject constructor(
    activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val schedulers: Schedulers

) : AbsPopupListener(
    getPlaylistBlockingUseCase = getPlaylistBlockingUseCase,
    addToPlaylistUseCase = addToPlaylistUseCase,
    schedulers = schedulers
) {

    private val activityRef = WeakReference(activity)


    private lateinit var genre: Genre
    private var song: Song? = null

    fun setData(container: View?, genre: Genre, song: Song?): GenrePopupListener {
        this.container = container
        this.genre = genre
        this.song = song
        return this
    }

    private fun getMediaId(): PresentationId {
        if (song != null) {
            return genre.presentationId.playableItem(song!!.id)
        } else {
            return genre.presentationId
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val activity = activityRef.get() ?: return true

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
            R.id.viewAlbum -> viewAlbum(navigator, song!!.albumPresentationId)
            R.id.viewArtist -> viewArtist(navigator, song!!.artistPresentationId)
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> AppShortcuts.instance(activity, schedulers)
                .addDetailShortcut(getMediaId().toDomain(), genre.name)
        }

        return true
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            navigator.toCreatePlaylistDialog(getMediaId().toDomain(), genre.size, genre.name)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        val mediaId = getMediaId().toDomain()
        require(mediaId is MediaId.Track)
        mediaProvider.playFromMediaId(mediaId, null, null)
    }

    private fun playShuffle() {
        val mediaId = getMediaId().toDomain()
        require(mediaId is MediaId.Category)
        mediaProvider.shuffle(mediaId, null)
    }

    private fun playLater() {
        if (song == null) {
            navigator.toPlayLater(getMediaId().toDomain(), genre.size, genre.name)
        } else {
            navigator.toPlayLater(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            navigator.toPlayNext(getMediaId().toDomain(), genre.size, genre.name)
        } else {
            navigator.toPlayNext(getMediaId().toDomain(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            navigator.toAddToFavoriteDialog(getMediaId().toDomain(), genre.size, genre.name)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            navigator.toDeleteDialog(getMediaId().toDomain(), genre.size, genre.name)
        } else {
            navigator.toDeleteDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

}