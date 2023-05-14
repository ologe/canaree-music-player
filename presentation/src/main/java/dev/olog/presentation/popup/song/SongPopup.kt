package dev.olog.presentation.popup.song

import android.provider.MediaStore
import android.view.View
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class SongPopup(
    view: View,
    song: Song,
    listener: SongPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_song)
        addPlaylistChooser(view.context, listener.getPlaylists(song.isPodcast))
        setOnMenuItemClickListener(listener)

        if (song.artist == MediaStore.UNKNOWN_STRING) {
            menu.removeItem(R.id.viewArtist)
        }
        if (song.album == MediaStore.UNKNOWN_STRING) {
            menu.removeItem(R.id.viewAlbum)
        }
    }

}