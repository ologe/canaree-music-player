package dev.olog.feature.playlist

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import dev.olog.feature.playlist.chooser.PlaylistChooserActivity
import dev.olog.feature.playlist.create.CreatePlaylistFragment
import dev.olog.feature.playlist.dialog.clear.ClearPlaylistDialog
import dev.olog.feature.playlist.dialog.create.NewPlaylistDialog
import dev.olog.feature.playlist.dialog.duplicates.RemoveDuplicatesDialog
import dev.olog.feature.playlist.dialog.rename.RenameDialog
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.navigation.createBackStackTag
import dev.olog.platform.navigation.superCerealTransition
import javax.inject.Inject

class FeaturePlaylistNavigatorImpl @Inject constructor(
    private val tags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
) : FeaturePlaylistNavigator {

    override fun toCreatePlaylist(activity: FragmentActivity, type: PlaylistType) {
        val newTag = createBackStackTag(CreatePlaylistFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = CreatePlaylistFragment.newInstance(type),
            tag = newTag,
            tags = tags,
        )
    }

    override fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun playlistChooserIntent(context: Context): Intent {
        return Intent(context, PlaylistChooserActivity::class.java)
    }

    override fun toRenameDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        itemTitle: String
    ) {
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
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

}