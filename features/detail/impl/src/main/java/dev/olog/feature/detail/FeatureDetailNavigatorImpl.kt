package dev.olog.feature.detail

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.detail.main.DetailFragment
import dev.olog.feature.detail.recently.added.RecentlyAddedFragment
import dev.olog.feature.detail.related.artist.RelatedArtistFragment
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.HasSlidingPanel
import dev.olog.platform.navigation.createBackStackTag
import dev.olog.platform.navigation.superCerealTransition
import dev.olog.shared.extension.findInContext
import dev.olog.ui.extension.collapse
import javax.inject.Inject

class FeatureDetailNavigatorImpl @Inject constructor(
    private val tags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
) : FeatureDetailNavigator {

    override fun toDetail(activity: FragmentActivity, mediaId: MediaId) {
        (activity.findInContext<HasSlidingPanel>()).getSlidingPanel().collapse()

        val newTag = createBackStackTag(DetailFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = DetailFragment.newInstance(mediaId),
            tag = newTag,
            tags = tags,
        )
    }

    override fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId) {
        val newTag = createBackStackTag(RelatedArtistFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = RelatedArtistFragment.newInstance(mediaId),
            tag = newTag,
            tags = tags,
        )
    }

    override fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId) {
        val newTag = createBackStackTag(RecentlyAddedFragment.TAG)
        superCerealTransition(
            activity = activity,
            fragment = RecentlyAddedFragment.newInstance(mediaId),
            tag = newTag,
            tags = tags,
        )
    }
}