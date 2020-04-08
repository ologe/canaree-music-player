package dev.olog.presentation.popup.song

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.domain.entity.track.Song
import dev.olog.domain.interactor.playlist.AddToPlaylistUseCase
import dev.olog.domain.interactor.playlist.GetPlaylistsUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.albumPresentationId
import dev.olog.feature.presentation.base.model.artistPresentationId
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.presentation.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import java.lang.ref.WeakReference
import javax.inject.Inject

internal class SongPopupListener @Inject constructor(
    activity: FragmentActivity,
    private val navigator: Navigator,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    schedulers: Schedulers

) : AbsPopupListener(
    getPlaylistBlockingUseCase = getPlaylistBlockingUseCase,
    addToPlaylistUseCase = addToPlaylistUseCase,
    schedulers = schedulers
) {

    private val activityRef = WeakReference(activity)

    private lateinit var song: Song

    fun setData(container: View?, song: Song): SongPopupListener {
        this.container = container
        this.song = song
        this.podcastPlaylist = this.song.isPodcast
        return this
    }

    private fun getMediaId(): PresentationId.Track {
        return song.presentationId
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val activity = activityRef.get() ?: return true

        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), -1, song.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, song.albumPresentationId)
            R.id.viewArtist -> viewArtist(navigator, song.artistPresentationId)
            R.id.share -> share(activity, song)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song)
        }


        return true
    }

    private fun toCreatePlaylist() {
        navigator.toCreatePlaylistDialog(getMediaId(), -1, song.title)
    }

    private fun playLater() {
        navigator.toPlayLater(getMediaId(), -1, song.title)
    }

    private fun playNext() {
        navigator.toPlayNext(getMediaId(), -1, song.title)
    }

    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(getMediaId(), -1, song.title)
    }

    private fun delete() {
        navigator.toDeleteDialog(getMediaId(), -1, song.title)
    }

}