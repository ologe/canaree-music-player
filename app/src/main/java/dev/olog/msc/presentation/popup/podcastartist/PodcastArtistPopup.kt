package dev.olog.msc.presentation.popup.podcastartist

import android.view.View
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.msc.R
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.presentation.AppConstants

class PodcastArtistPopup (
    view: View,
    artist: Artist,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_podcast_artist)
        } else {
            inflate(R.menu.dialog_podcast)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song != null){
            menu.removeItem(R.id.viewArtist)

            if (song.album == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}