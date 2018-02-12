package dev.olog.msc.presentation.popup.genre

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.Popup
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class GenrePopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase.execute(), addToPlaylistUseCase) {

    private lateinit var genre: Genre
    private var song: Song? = null

    fun setData(genre: Genre, song: Song?): GenrePopupListener{
        this.genre = genre
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null){
            return MediaId.playableItem(MediaId.genreId(genre.id), song!!.id)
        } else {
            return MediaId.genreId(genre.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId())

        when (itemId){
            Popup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.addToQueue -> addToQueue()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.artistId(song!!.artistId))
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
        }

        return true
    }

    private fun toCreatePlaylist(){
        navigator.toCreatePlaylistDialog(getMediaId())
    }

    private fun playFromMediaId(){
        mediaProvider.playFromMediaId(getMediaId())
    }

    private fun playShuffle(){
        mediaProvider.shuffle(getMediaId())
    }

    private fun addToQueue(){
        if (song == null){
            navigator.toAddToQueueDialog(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toAddToQueueDialog(getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite(){
        if (song == null){
            navigator.toAddToFavoriteDialog(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, song!!.title)
        }
    }

    private fun delete(){
        if (song == null){
            navigator.toDeleteDialog(getMediaId(), genre.size, genre.name)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, song!!.title)
        }
    }

}