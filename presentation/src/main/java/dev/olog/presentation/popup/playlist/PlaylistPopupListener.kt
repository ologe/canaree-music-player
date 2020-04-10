package dev.olog.presentation.popup.playlist

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.feature.app.shortcuts.AppShortcuts
import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Playlist
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
import dev.olog.feature.presentation.base.extensions.toast
import java.lang.ref.WeakReference
import javax.inject.Inject

internal class PlaylistPopupListener @Inject constructor(
    activity: FragmentActivity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val schedulers: Schedulers

) : AbsPopupListener(
    getPlaylistBlockingUseCase = getPlaylistBlockingUseCase,
    addToPlaylistUseCase = addToPlaylistUseCase,
    schedulers = schedulers,
    activity = activity
) {

    private lateinit var playlist: Playlist
    private var song: Song? = null

    fun setData(container: View?, playlist: Playlist, song: Song?): PlaylistPopupListener {
        this.container = container
        this.playlist = playlist
        this.song = song
        this.podcastPlaylist = this.playlist.isPodcast
        return this
    }

    private fun getMediaId(): PresentationId {
        if (song != null) {
            return playlist.presentationId.playableItem(song!!.id)
        } else {
            return playlist.presentationId
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
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, song!!.albumPresentationId)
            R.id.viewArtist -> viewArtist(navigator, song!!.artistPresentationId)
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> AppShortcuts.instance(activity, schedulers)
                .addDetailShortcut(getMediaId().toDomain(), playlist.title)
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        navigator.toRemoveDuplicatesDialog(playlist.presentationId.toDomain(), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            navigator.toCreatePlaylistDialog(getMediaId().toDomain(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            val mediaId = getMediaId().toDomain()
            require(mediaId is MediaId.Track)
            mediaProvider.playFromMediaId(mediaId, null, null)
        }
    }

    private fun playShuffle() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            val mediaId = getMediaId().toDomain()
            require(mediaId is MediaId.Category)
            mediaProvider.shuffle(mediaId, null)
        }
    }

    private fun playLater() {
        if (song == null) {
            navigator.toPlayLater(getMediaId().toDomain(), playlist.size, playlist.title)
        } else {
            navigator.toPlayLater(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            navigator.toPlayNext(getMediaId().toDomain(), playlist.size, playlist.title)
        } else {
            navigator.toPlayNext(getMediaId().toDomain(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            navigator.toAddToFavoriteDialog(getMediaId().toDomain(), playlist.size, playlist.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            navigator.toDeleteDialog(getMediaId().toDomain(), playlist.size, playlist.title)
        } else {
            navigator.toDeleteDialog(getMediaId().toDomain(), -1, song!!.title)
        }
    }

    private fun rename() {
        navigator.toRenameDialog(playlist.presentationId.toDomain(), playlist.title)
    }

    private fun clearPlaylist() {
        navigator.toClearPlaylistDialog(playlist.presentationId.toDomain(), playlist.title)
    }


}