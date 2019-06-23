package dev.olog.msc.presentation.popup.podcastalbum

import android.app.Activity
import android.view.MenuItem
import dev.olog.core.MediaId
import dev.olog.core.entity.podcast.Podcast
import dev.olog.core.entity.podcast.PodcastAlbum
import dev.olog.core.entity.podcast.toSong
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import javax.inject.Inject

class PodcastAlbumPopupListener @Inject constructor(
    private val activity: Activity,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, true) {

    private lateinit var album: PodcastAlbum
    private var podcast: Podcast? = null

    fun setData(album: PodcastAlbum, podcast: Podcast?): PodcastAlbumPopupListener{
        this.album = album
        this.podcast = podcast
        return this
    }

    private fun getMediaId(): MediaId {
        if (podcast != null){
            return MediaId.playableItem(MediaId.podcastAlbumId(album.id), podcast!!.id)
        } else {
            return MediaId.podcastAlbumId(album.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), album.songs, album.title)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewArtist -> viewArtist()
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.podcastAlbumId(podcast!!.albumId))
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.share -> share(activity, podcast!!.toSong())
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), album.title)
        }

        return true
    }

    private fun toCreatePlaylist(){
        if (podcast == null){
            navigator.toCreatePlaylistDialog(getMediaId(), album.songs, album.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playFromMediaId(){
        mediaProvider.playFromMediaId(getMediaId())
    }

    private fun playShuffle(){
        mediaProvider.shuffle(getMediaId())
    }

    private fun playLater(){
        if (podcast == null){
            navigator.toPlayLater(getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayLater(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playNext(){
        if (podcast == null){
            navigator.toPlayNext(getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayNext(getMediaId(), -1, podcast!!.title)
        }
    }


    private fun addToFavorite(){
        if (podcast == null){
            navigator.toAddToFavoriteDialog(getMediaId(), album.songs, album.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun delete(){
        if (podcast == null){
            navigator.toDeleteDialog(getMediaId(), album.songs, album.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun viewArtist(){
        navigator.toDetailFragment(MediaId.podcastArtistId(album.artistId))
    }


}