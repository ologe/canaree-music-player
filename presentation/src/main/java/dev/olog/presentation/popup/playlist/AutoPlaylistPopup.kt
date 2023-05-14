package dev.olog.presentation.popup.playlist

import android.view.View
import dev.olog.core.entity.track.AutoPlaylist
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class AutoPlaylistPopup(
    view: View,
    playlist: AutoPlaylist,
    listener: AutoPlaylistPopupListener
) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_auto_playlist)

        addPlaylistChooser(view.context, listener.getPlaylists(playlist.isPodcast))

        setOnMenuItemClickListener(listener)

        if (playlist.isLastAdded) {
            menu.removeItem(R.id.clear)
        }
        if (playlist.isFavorite) {
            menu.removeItem(R.id.addToFavorite)
        }
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