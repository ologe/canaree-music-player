package dev.olog.msc.presentation.popup.podcast

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class PodcastPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, true) {

    private lateinit var podcast: Podcast

    fun setData(podcast: Podcast): PodcastPopupListener{
        this.podcast = podcast
        return this
    }

    private fun getMediaId(): MediaId {
        return MediaId.podcastId(podcast.id)
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), -1, podcast.title)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.podcastAlbumId(podcast.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.podcastArtistId(podcast.artistId))
        }


        return true
    }

    private fun toCreatePlaylist(){
        navigator.toCreatePlaylistDialog(getMediaId(), -1, podcast.title)
    }

    private fun playLater(){
        navigator.toPlayLater(getMediaId(), -1, podcast.title)
    }

    private fun playNext(){
        navigator.toPlayNext(getMediaId(), -1, podcast.title)
    }

    private fun addToFavorite(){
        navigator.toAddToFavoriteDialog(getMediaId(), -1, podcast.title)
    }

    private fun delete(){
        navigator.toDeleteDialog(getMediaId(), -1, podcast.title)
    }

}