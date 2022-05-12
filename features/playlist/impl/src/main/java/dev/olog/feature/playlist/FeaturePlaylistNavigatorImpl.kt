package dev.olog.feature.playlist

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.feature.playlist.chooser.PlaylistChooserActivity
import dev.olog.feature.playlist.create.CreatePlaylistFragment
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.createBackStackTag
import dev.olog.platform.superCerealTransition
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
//        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle) todo
//        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun playlistChooserIntent(context: Context): Intent {
        return Intent(context, PlaylistChooserActivity::class.java)
    }
}