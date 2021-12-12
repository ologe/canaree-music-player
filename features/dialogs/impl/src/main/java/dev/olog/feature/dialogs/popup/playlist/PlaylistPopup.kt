package dev.olog.feature.dialogs.popup.playlist

import android.view.View
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.feature.dialogs.R
import dev.olog.feature.dialogs.popup.AbsPopup
import dev.olog.feature.dialogs.popup.AbsPopupListener

class PlaylistPopup(
    view: View,
    playlist: Playlist,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null) {
            inflate(R.menu.dialog_playlist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null) {
            if (Playlist.isAutoPlaylist(playlist.id)) {
                menu.removeItem(R.id.rename)
                menu.removeItem(R.id.delete)
                menu.removeItem(R.id.removeDuplicates)
            }
            if (Playlist.isAutoPlaylist(playlist.id) || !Playlist.isAutoPlaylist(playlist.id)) {
                menu.removeItem(R.id.clear)
            }
            if (playlist.size < 1) {
                menu.removeItem(R.id.play)
                menu.removeItem(R.id.playShuffle)
                menu.removeItem(R.id.addToFavorite)
                menu.removeItem(R.id.addToPlaylist)
                menu.removeItem(R.id.playLater)
                menu.removeItem(R.id.playNext)
            }
        } else {
            if (Playlist.isFavorite(playlist.id)) {
                menu.removeItem(R.id.addToFavorite)
            }
        }
    }

}