package dev.olog.msc.presentation.popup.podcast

import android.view.View
import dev.olog.core.entity.track.Song
import dev.olog.msc.R
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.presentation.AppConstants

class PodcastPopup(
    view: View,
    podcast: Song,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_podcast)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (podcast.artist == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewArtist)
        }
        if (podcast.album == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewAlbum)
        }
    }

}