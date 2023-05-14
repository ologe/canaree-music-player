package dev.olog.presentation.popup.playlist

import android.view.View
import dev.olog.core.entity.track.Playlist
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class PlaylistPopup(
    view: View,
    playlist: Playlist,
    listener: PlaylistPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_playlist)
        addPlaylistChooser(view.context, listener.getPlaylists(playlist.isPodcast))
        setOnMenuItemClickListener(listener)

        if (playlist.size < 1) {
            menu.removeItem(R.id.play)
            menu.removeItem(R.id.playShuffle)
            menu.removeItem(R.id.addToFavorite)
            menu.removeItem(R.id.addToPlaylist)
            menu.removeItem(R.id.playLater)
            menu.removeItem(R.id.playNext)
            menu.removeItem(R.id.clear)
        }
    }

}