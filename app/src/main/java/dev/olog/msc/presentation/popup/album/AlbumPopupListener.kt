package dev.olog.msc.presentation.popup.album

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.Popup
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class AlbumPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase,
        private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase.execute(), addToPlaylistUseCase) {

    private lateinit var album: Album
    private var song: Song? = null

    fun setData(album: Album, song: Song?): AlbumPopupListener{
        this.album = album
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null){
            return MediaId.playableItem(MediaId.albumId(album.id), song!!.id)
        } else {
            return MediaId.albumId(album.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), album.songs, album.title)

        when (itemId){
            Popup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.addToQueue -> addToQueue()
            R.id.delete -> delete()
            R.id.viewArtist -> viewArtist()
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song!!.albumId))
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), album.title, album.image)
        }

        return true
    }

    private fun toCreatePlaylist(){
        if (song == null){
            navigator.toCreatePlaylistDialog(getMediaId(), album.songs, album.title)
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
            navigator.toAddToQueueDialog(getMediaId(), album.songs, album.title)
        } else {
            navigator.toAddToQueueDialog(getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite(){
        if (song == null){
            navigator.toAddToFavoriteDialog(getMediaId(), album.songs, album.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun delete(){
        if (song == null){
            navigator.toDeleteDialog(getMediaId(), album.songs, album.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun viewArtist(){
        navigator.toDetailFragment(MediaId.artistId(album.artistId))
    }


}