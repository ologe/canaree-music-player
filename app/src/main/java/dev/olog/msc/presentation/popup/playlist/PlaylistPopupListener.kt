package dev.olog.msc.presentation.popup.playlist

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.Popup
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class PlaylistPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase.execute(), addToPlaylistUseCase) {

    private lateinit var playlist: Playlist
    private var song: Song? = null

    fun setData(playlist: Playlist, song: Song?): PlaylistPopupListener{
        this.playlist = playlist
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null){
            return MediaId.playableItem(MediaId.playlistId(playlist.id), song!!.id)
        } else {
            return MediaId.playlistId(playlist.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), playlist.size, playlist.title)

        when (itemId){
            Popup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.addToQueue -> addToQueue()
            R.id.delete -> delete()
            R.id.rename -> rename()
            R.id.clear -> clearPlaylist()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.artistId(song!!.artistId))
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
        }


        return true
    }

    private fun toCreatePlaylist(){
        if (song == null){
            navigator.toCreatePlaylistDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId(){
        mediaProvider.playFromMediaId(getMediaId())
    }

    private fun playShuffle(){
        mediaProvider.shuffle(getMediaId())
    }

    private fun addToQueue(){
        if (song == null){
            navigator.toAddToQueueDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toAddToQueueDialog(getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite(){
        if (song == null){
            navigator.toAddToFavoriteDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun delete(){
        if (song == null){
            navigator.toDeleteDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun rename(){
        navigator.toRenameDialog(getMediaId(), playlist.title)
    }

    private fun clearPlaylist(){
        navigator.toClearPlaylistDialog(getMediaId(), playlist.title)
    }


}