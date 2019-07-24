package dev.olog.msc.presentation.popup.folder

import android.app.Activity
import android.view.MenuItem
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.core.entity.track.*
import dev.olog.media.MediaProvider
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.presentation.navigator.Navigator
import javax.inject.Inject

class FolderPopupListener @Inject constructor(
    private val activity: Activity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var folder: Folder
    private var song: Song? = null

    fun setData(folder: Folder, song: Song?): FolderPopupListener {
        this.folder = folder
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null){
            val folderMediaId = folder.getMediaId()
            return MediaId.playableItem(folderMediaId, song!!.id)
        } else {
            return folder.getMediaId()
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), folder.size, folder.title)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, song!!.getAlbumMediaId())
            R.id.viewArtist -> viewArtist(navigator, song!!.getArtistMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> AppShortcuts.instance(activity).addDetailShortcut(getMediaId(), folder.title)
        }


        return true
    }

    private fun toCreatePlaylist(){
        if (song == null){
            navigator.toCreatePlaylistDialog(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId(){
        mediaProvider.playFromMediaId(getMediaId(), null, null)
    }

    private fun playShuffle(){
        mediaProvider.shuffle(getMediaId(), null)
    }

    private fun playLater(){
        if (song == null){
            navigator.toPlayLater(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toPlayLater(getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext(){
        if (song == null){
            navigator.toPlayNext(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toPlayNext(getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite(){
        if (song == null){
            navigator.toAddToFavoriteDialog(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun delete(){
        if (song == null){
            navigator.toDeleteDialog(getMediaId(), folder.size, folder.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, song!!.title)
        }
    }

}