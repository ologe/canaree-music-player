package dev.olog.feature.playlist

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType

interface FeaturePlaylistNavigator {

    fun toChooseTracksForPlaylistFragment(
        activity: FragmentActivity,
        type: PlaylistType
    )

    fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toRemoveDuplicatesDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    )

    fun toClearPlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    )

    fun toRenameDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    )

}