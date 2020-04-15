package dev.olog.presentation.popup.artist

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Song
import dev.olog.domain.interactor.playlist.AddToPlaylistUseCase
import dev.olog.domain.interactor.playlist.GetPlaylistsUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.app.shortcuts.AppShortcuts
import dev.olog.feature.presentation.base.model.*
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import javax.inject.Inject

internal class ArtistPopupListener @Inject constructor(
    activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    schedulers: Schedulers,
    private val appShortcuts: AppShortcuts

) : AbsPopupListener(
    getPlaylistBlockingUseCase = getPlaylistBlockingUseCase,
    addToPlaylistUseCase = addToPlaylistUseCase,
    schedulers = schedulers,
    activity = activity
) {

    private lateinit var artist: Artist
    private var song: Song? = null

    fun setData(container: View?, artist: Artist, song: Song?): ArtistPopupListener {
        this.container = container
        this.artist = artist
        this.song = song
        this.podcastPlaylist = this.artist.isPodcast
        return this
    }

    private fun getMediaId(): PresentationId {
        if (song != null) {
            return artist.presentationId.playableItem(song!!.id)
        } else {
            return artist.presentationId
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
            R.id.viewAlbum -> viewAlbum(navigator, song!!.albumPresentationId)
            R.id.viewArtist -> viewArtist(navigator, song!!.artistPresentationId)
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId().toDomain(), artist.name)
        }


        return true
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            navigator.toCreatePlaylistDialog(getMediaId().toDomain(), artist.songs, artist.name)
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
            navigator.toPlayLater(getMediaId().toDomain(), artist.songs, artist.name)
        } else {
            navigator.toPlayLater(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            navigator.toPlayNext(getMediaId().toDomain(), artist.songs, artist.name)
        } else {
            navigator.toPlayNext(getMediaId().toDomain(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            navigator.toAddToFavoriteDialog(getMediaId().toDomain(), artist.songs, artist.name)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            navigator.toDeleteDialog(getMediaId().toDomain(), artist.songs, artist.name)
        } else {
            navigator.toDeleteDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

}