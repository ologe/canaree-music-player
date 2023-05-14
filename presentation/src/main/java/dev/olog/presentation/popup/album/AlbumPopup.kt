package dev.olog.presentation.popup.album

import android.provider.MediaStore
import android.view.View
import dev.olog.core.entity.track.Album
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class AlbumPopup(
    view: View,
    album: Album,
    listener: AlbumPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_album)
        addPlaylistChooser(view.context, listener.getPlaylists(album.isPodcast))
        setOnMenuItemClickListener(listener)

        if (album.artist == MediaStore.UNKNOWN_STRING) {
            menu.removeItem(R.id.viewArtist)
        }
    }

}