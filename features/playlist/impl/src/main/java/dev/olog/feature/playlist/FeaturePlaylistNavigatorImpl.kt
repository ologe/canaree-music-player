package dev.olog.feature.playlist

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
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

}