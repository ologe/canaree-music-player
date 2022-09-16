package dev.olog.feature.main.popup.folder

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import dev.olog.feature.shortcuts.api.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.edit.api.FeatureEditNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import dev.olog.feature.main.R
import dev.olog.feature.main.popup.AbsPopup
import dev.olog.feature.main.popup.AbsPopupListener
import javax.inject.Inject

class FolderPopupListener @Inject constructor(
    private val activity: FragmentActivity,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val featurePlaylistNavigator: FeaturePlaylistNavigator,
    private val featureDetailNavigator: FeatureDetailNavigator,
    private val featureEditNavigator: FeatureEditNavigator,
    private val featureMainNavigator: FeatureMainNavigator,
    private val appShortcuts: AppShortcuts,
) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var folder: Folder
    private var song: Song? = null

    fun setData(folder: Folder, song: Song?): FolderPopupListener {
        this.folder = folder
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null) {
            val folderMediaId = folder.getMediaId()
            return MediaId.playableItem(folderMediaId, song!!.id)
        } else {
            return folder.getMediaId()
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId.toString(), getMediaId(), folder.size, folder.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(activity, featureEditNavigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(activity, featureDetailNavigator, song!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(activity, featureDetailNavigator, song!!.getArtistMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(activity, featureMainNavigator, getMediaId(), song!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(
                mediaId = getMediaId(),
                title = folder.title
            )
        }


        return true
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), folder.size, folder.title)
        } else {
            featurePlaylistNavigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        mediaProvider.playFromMediaId(getMediaId(), null)
    }

    private fun playShuffle() {
        mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater() {
        if (song == null) {
            featureMainNavigator.toPlayLater(activity, getMediaId(), folder.size, folder.title)
        } else {
            featureMainNavigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            featureMainNavigator.toPlayNext(activity, getMediaId(), folder.size, folder.title)
        } else {
            featureMainNavigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        if (song == null) {
            featureMainNavigator.toAddToFavoriteDialog(activity, getMediaId(), folder.size, folder.title)
        } else {
            featureMainNavigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun delete() {
        if (song == null) {
            featureMainNavigator.toDeleteDialog(activity, getMediaId(), folder.size, folder.title)
        } else {
            featureMainNavigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

}