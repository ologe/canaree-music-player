package dev.olog.presentation.popup.song

import android.view.View
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener

internal class SongPopup(
    view: View,
    listener: AbsPopupListener,
    song: Song

) : AbsPopup(view) {

    init {
        val menu = if (song.isPodcast) R.menu.dialog_podcast else R.menu.dialog_song
        inflate(menu)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)
    }

}