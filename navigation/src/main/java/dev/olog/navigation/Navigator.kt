package dev.olog.navigation

import android.view.View
import dev.olog.core.MediaId

interface Navigator {

    fun toFirstAccess()

    fun toDetailFragment(mediaId: MediaId)

    fun toRecentlyAdded(mediaId: MediaId)

    fun toRelatedArtists(mediaId: MediaId)

    fun toDialog(mediaId: MediaId, view: View)

}