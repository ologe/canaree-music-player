package dev.olog.feature.playlist

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.playlist.clear.ClearPlaylistDialog
import dev.olog.feature.playlist.create.dialog.NewPlaylistDialog
import dev.olog.feature.playlist.duplicates.RemoveDuplicatesDialog
import dev.olog.feature.playlist.rename.RenameDialog
import javax.inject.Inject

class FeaturePlaylistNavigatorImpl @Inject constructor(

) : FeaturePlaylistNavigator {

    override fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    ) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }

    override fun toRemoveDuplicatesDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    ) {
        val fragment = RemoveDuplicatesDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RemoveDuplicatesDialog.TAG)
    }

    override fun toRenameDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    ) {
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }
}