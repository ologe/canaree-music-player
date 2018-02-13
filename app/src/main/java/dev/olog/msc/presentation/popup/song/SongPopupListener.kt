package dev.olog.msc.presentation.popup.song

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.Popup
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class SongPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase.execute(), addToPlaylistUseCase) {

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
            Popup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.addToQueue -> addToQueue()
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

    private fun playFromMediaId(){
        mediaProvider.playFromMediaId(getMediaId())
    }

    private fun playShuffle(){
        mediaProvider.shuffle(getMediaId())
    }

    private fun addToQueue(){
        navigator.toAddToQueueDialog(getMediaId(), -1, song.title)
    }

    private fun addToFavorite(){
        navigator.toAddToFavoriteDialog(getMediaId(), -1, song.title)
    }

    private fun delete(){
        navigator.toDeleteDialog(getMediaId(), -1, song.title)
    }

}