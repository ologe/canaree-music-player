package dev.olog.feature.detail

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.base.HasSlidingPanel
import dev.olog.feature.base.createBackStackTag
import dev.olog.feature.base.superCerealTransition
import dev.olog.feature.detail.detail.DetailFragment
import dev.olog.feature.detail.recently.added.RecentlyAddedFragment
import dev.olog.feature.detail.related.artist.RelatedArtistFragment
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.widgets.extension.collapse
import javax.inject.Inject

class FeatureDetailNavigatorImpl @Inject constructor(

) : FeatureDetailNavigator {

    override fun toDetailFragment(activity: FragmentActivity, mediaId: MediaId) {
        (activity.findInContext<HasSlidingPanel>()).getSlidingPanel().collapse()

        val newTag = createBackStackTag(DetailFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = DetailFragment.newInstance(mediaId),
            tag = newTag
        )
    }

    override fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId) {
        val newTag = createBackStackTag(RelatedArtistFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = RelatedArtistFragment.newInstance(mediaId),
            tag = newTag
        )
    }

    override fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId) {
        val newTag = createBackStackTag(RecentlyAddedFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = RecentlyAddedFragment.newInstance(mediaId),
            tag = newTag
        )
    }
}