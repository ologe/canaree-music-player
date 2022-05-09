package dev.olog.feature.playlist

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId

interface FeaturePlaylistNavigator {

    fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

}