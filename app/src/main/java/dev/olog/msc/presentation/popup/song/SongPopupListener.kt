package dev.olog.msc.presentation.popup.song

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.core.entity.Song
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.core.MediaId
import javax.inject.Inject

class SongPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var song: Song

    fun setData(song: Song): SongPopupListener{
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        return MediaId.songId(song.id)
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), -1, song.title)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.artistId(song.artistId))
            R.id.share -> share(activity, song)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song)
        }


        return true
    }

    private fun toCreatePlaylist(){
        navigator.toCreatePlaylistDialog(getMediaId(), -1, song.title)
    }

    private fun playLater(){
        navigator.toPlayLater(getMediaId(), -1, song.title)
    }

    private fun playNext(){
        navigator.toPlayNext(getMediaId(), -1, song.title)
    }

    private fun addToFavorite(){
        navigator.toAddToFavoriteDialog(getMediaId(), -1, song.title)
    }

    private fun delete(){
        navigator.toDeleteDialog(getMediaId(), -1, song.title)
    }

}