package dev.olog.feature.dialog.popup.playlist

import android.view.View
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Track
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.popup.AbsPopup
import dev.olog.feature.dialog.popup.AbsPopupListener

class PlaylistPopup(
    view: View,
    playlist: Playlist,
    track: Track?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (track == null) {
            inflate(R.menu.dialog_playlist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (track == null) {
            if (AutoPlaylist.isAutoPlaylist(playlist.id)) {
                menu.removeItem(R.id.rename)
                menu.removeItem(R.id.delete)
                menu.removeItem(R.id.removeDuplicates)
            }
            if (playlist.id == AutoPlaylist.LAST_ADDED.id || !AutoPlaylist.isAutoPlaylist(playlist.id)) {
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
            if (playlist.id == AutoPlaylist.FAVORITE.id) {
                menu.removeItem(R.id.addToFavorite)
            }
        }
    }

}