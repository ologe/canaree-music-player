package dev.olog.presentation.popup.album

import android.view.View
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import dev.olog.shared.AppConstants

class AlbumPopup(
    view: View,
    album: Album,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null) {
            inflate(R.menu.dialog_album)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null) {
            if (album.artist == AppConstants.UNKNOWN) {
                menu.removeItem(R.id.viewArtist)
            }
        } else {
            menu.removeItem(R.id.viewAlbum)

            if (song.artist == AppConstants.UNKNOWN) {
                menu.removeItem(R.id.viewArtist)
            }
        }
    }

}