package dev.olog.msc.presentation.popup.podcastartist

import android.app.Activity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.core.entity.Podcast
import dev.olog.core.entity.PodcastArtist
import dev.olog.core.entity.toSong
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.core.MediaId
import javax.inject.Inject

class PodcastArtistPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase,
        private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, true) {

    private lateinit var artist: PodcastArtist
    private var podcast: Podcast? = null

    fun setData(artist: PodcastArtist, podcast: Podcast?): PodcastArtistPopupListener{
        this.artist = artist
        this.podcast = podcast
        return this
    }

    private fun getMediaId(): MediaId {
        if (podcast != null){
            return MediaId.playableItem(MediaId.podcastArtistId(artist.id), podcast!!.id)
        } else {
            return MediaId.podcastArtistId(artist.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), artist.songs, artist.name)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.podcastAlbumId(podcast!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.podcastArtistId(podcast!!.artistId))
            R.id.share -> share(activity, podcast!!.toSong())
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), artist.name, artist.image)
        }


        return true
    }

    private fun toCreatePlaylist(){
        if (podcast == null){
            navigator.toCreatePlaylistDialog(getMediaId(), artist.songs, artist.name)
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
            navigator.toPlayLater(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toPlayLater(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playNext(){
        if (podcast == null){
            navigator.toPlayNext(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toPlayNext(getMediaId(), -1, podcast!!.title)
        }
    }



    private fun addToFavorite(){
        if (podcast == null){
            navigator.toAddToFavoriteDialog(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun delete(){
        if (podcast == null){
            navigator.toDeleteDialog(getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, podcast!!.title)
        }
    }

}