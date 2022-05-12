package dev.olog.feature.playlist

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType

interface FeaturePlaylistNavigator {

    fun toCreatePlaylist(
        activity: FragmentActivity,
        type: PlaylistType
    )

    fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun playlistChooserIntent(context: Context): Intent

}