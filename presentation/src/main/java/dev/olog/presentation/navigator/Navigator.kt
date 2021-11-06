package dev.olog.presentation.navigator

import android.view.View
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType

interface Navigator {

    fun toFirstAccess()

    fun toDetailFragment(mediaId: MediaId)

    fun toRelatedArtists(mediaId: MediaId)

    fun toRecentlyAdded(mediaId: MediaId)

    fun toChooseTracksForPlaylistFragment(type: PlaylistType)

    fun toOfflineLyrics()

    fun toDialog(mediaId: MediaId, anchor: View)

    fun toMainPopup(anchor: View, category: MediaIdCategory?)

}