package dev.olog.msc.presentation.popup.podcastplaylist

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class PodcastPlaylistPopup(
        view: View,
        playlist: PodcastPlaylist,
        song: Podcast?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_podcast_playlist)
        } else {
            inflate(R.menu.dialog_podcast)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null){
            if (PlaylistConstants.isAutoPlaylist(playlist.id)){
                menu.removeItem(R.id.rename)
                menu.removeItem(R.id.delete)
                menu.removeItem(R.id.removeDuplicates)
            }
            if (playlist.id == PlaylistConstants.LAST_ADDED_ID){
                menu.removeItem(R.id.clear)
            }
            if (playlist.size < 1){
                menu.removeItem(R.id.play)
                menu.removeItem(R.id.playShuffle)
                menu.removeItem(R.id.addToFavorite)
                menu.removeItem(R.id.playLater)
                menu.removeItem(R.id.playNext)
                menu.removeItem(R.id.addToPlaylist)
                menu.removeItem(R.id.clear)
            }
        } else {
            if (song.artist == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
            if (song.album == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
            if (playlist.id == PlaylistConstants.FAVORITE_LIST_ID){
                menu.removeItem(R.id.addToFavorite)
            }
        }
    }

}