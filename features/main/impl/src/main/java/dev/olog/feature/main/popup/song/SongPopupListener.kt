package dev.olog.feature.main.popup.song

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.edit.api.FeatureEditNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.R
import dev.olog.feature.main.popup.AbsPopup
import dev.olog.feature.main.popup.AbsPopupListener
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import java.lang.ref.WeakReference
import javax.inject.Inject

class SongPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featurePlaylistNavigator: FeaturePlaylistNavigator,
    private val featureDetailNavigator: FeatureDetailNavigator,
    private val featureEditNavigator: FeatureEditNavigator,
    private val featureMainNavigator: FeatureMainNavigator,
) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private val activityRef = WeakReference(activity)


    private lateinit var song: Song

    fun setData(song: Song): SongPopupListener {
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        return song.getMediaId()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val activity = activityRef.get() ?: return true

        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId.toString(), getMediaId(), -1, song.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(activity, featureEditNavigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, featureDetailNavigator, song.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, featureDetailNavigator, song.getArtistMediaId())
            R.id.share -> share(activity, song)
            R.id.setRingtone -> setRingtone(activity, featureMainNavigator, getMediaId(), song)
        }


        return true
    }

    private fun toCreatePlaylist() {
        featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song.title)
    }

    private fun playLater() {
        featureMainNavigator.toPlayLater(activity, getMediaId(), -1, song.title)
    }

    private fun playNext() {
        featureMainNavigator.toPlayNext(activity, getMediaId(), -1, song.title)
    }

    private fun addToFavorite() {
        featureMainNavigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song.title)
    }

    private fun delete() {
        featureMainNavigator.toDeleteDialog(activity, getMediaId(), -1, song.title)
    }

}