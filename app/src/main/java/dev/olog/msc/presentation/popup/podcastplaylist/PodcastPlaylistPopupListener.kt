package dev.olog.msc.presentation.popup.podcastplaylist

import android.app.Activity
import android.view.MenuItem
import androidx.core.widget.toast
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.entity.toSong
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class PodcastPlaylistPopupListener @Inject constructor(
        private val activity: Activity,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase,
        private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, true) {

    private lateinit var playlist: PodcastPlaylist
    private var podcast: Podcast? = null

    fun setData(playlist: PodcastPlaylist, podcast: Podcast?): PodcastPlaylistPopupListener {
        this.playlist = playlist
        this.podcast = podcast
        return this
    }

    private fun getMediaId(): MediaId {
        if (podcast != null){
            return MediaId.playableItem(MediaId.podcastPlaylistId(playlist.id), podcast!!.id)
        } else {
            return MediaId.podcastPlaylistId(playlist.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), playlist.size, playlist.title)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.rename -> rename()
            R.id.clear -> clearPlaylist()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.podcastAlbumId(podcast!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.podcastArtistId(podcast!!.artistId))
            R.id.share -> share(activity, podcast!!.toSong())
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), playlist.title, playlist.image)
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates(){
        navigator.toRemoveDuplicatesDialog(MediaId.podcastPlaylistId(playlist.id), playlist.title)
    }

    private fun toCreatePlaylist(){
        if (podcast == null){
            navigator.toCreatePlaylistDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylistDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playFromMediaId(){
        if (playlist.size == 0){
            activity.toast(R.string.common_empty_list)
        } else {
            mediaProvider.playFromMediaId(getMediaId())
        }
    }

    private fun playShuffle(){
        if (playlist.size == 0){
            activity.toast(R.string.common_empty_list)
        } else {
            mediaProvider.shuffle(getMediaId())
        }
    }

    private fun playLater(){
        if (podcast == null){
            navigator.toPlayLater(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayLater(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playNext(){
        if (podcast == null){
            navigator.toPlayNext(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayNext(getMediaId(), -1, podcast!!.title)
        }
    }


    private fun addToFavorite(){
        if (podcast == null){
            navigator.toAddToFavoriteDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toAddToFavoriteDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun delete(){
        if (podcast == null){
            navigator.toDeleteDialog(getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toDeleteDialog(getMediaId(), -1, podcast!!.title)
        }
    }

    private fun rename(){
        navigator.toRenameDialog(getMediaId(), playlist.title)
    }

    private fun clearPlaylist(){
        navigator.toClearPlaylistDialog(getMediaId(), playlist.title)
    }


}