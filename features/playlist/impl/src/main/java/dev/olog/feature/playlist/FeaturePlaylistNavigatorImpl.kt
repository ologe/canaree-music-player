package dev.olog.feature.playlist

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.playlist.chooser.PlaylistChooserActivity
import javax.inject.Inject

class FeaturePlaylistNavigatorImpl @Inject constructor(

) : FeaturePlaylistNavigator {

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