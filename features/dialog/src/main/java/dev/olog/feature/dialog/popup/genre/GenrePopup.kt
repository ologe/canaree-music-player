package dev.olog.feature.dialog.popup.genre

import android.view.View
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Track
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.popup.AbsPopup
import dev.olog.feature.dialog.popup.AbsPopupListener
import dev.olog.shared.android.utils.isQ

class GenrePopup(
    view: View,
    @Suppress("UNUSED_PARAMETER") genre: Genre,
    track: Track?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (track == null) {
            inflate(R.menu.dialog_genre)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (isQ() && track == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }
    }

}