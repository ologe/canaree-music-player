package dev.olog.msc.presentation.popup.playlist

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class PlaylistPopup(
        view: View,
        playlist: Playlist,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_playlist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null){
            if (PlaylistConstants.isAutoPlaylist(playlist.id)){
                menu.removeItem(R.id.rename)
                menu.removeItem(R.id.delete)
            }
            if (playlist.id == PlaylistConstants.LAST_ADDED_ID){
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