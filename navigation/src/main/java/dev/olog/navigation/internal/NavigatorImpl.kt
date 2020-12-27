package dev.olog.navigation.internal

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dev.olog.core.MediaId
import dev.olog.navigation.BottomNavigator
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.navigation.utils.findFirstVisibleFragment
import javax.inject.Inject
import javax.inject.Provider

internal class NavigatorImpl @Inject constructor(
    bottomNavigator: BottomNavigatorImpl,
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : BaseNavigator(),
    Navigator,
    BottomNavigator by bottomNavigator {

    override fun toFirstAccess() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.ONBOARDING]?.get()
        val tag = FragmentScreen.ONBOARDING.tag
        replaceFragment(activity, fragment, tag, android.R.id.content, forced = true)
    }

    override fun toDetailFragment(mediaId: MediaId) {
        // TODO collapse sliding panel here
        toDetailScreen(FragmentScreen.DETAIL, mediaId)
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        toDetailScreen(FragmentScreen.RELATED_ARTISTS, mediaId)
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        toDetailScreen(FragmentScreen.RECENTLY_ADDED, mediaId)
    }

    private fun toDetailScreen(screen: FragmentScreen, mediaId: MediaId) {
        val activity = activityProvider() ?: return
        val fragment = fragments[screen]?.get()
        val tag = createBackStackTag(screen.tag)

        fragment?.arguments = bundleOf(
            Params.MEDIA_ID to mediaId.toString(),
        )

        val topFragment = activity.supportFragmentManager.findFirstVisibleFragment()
        addFragment(activity, fragment, tag) {
            addToBackStack(tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            topFragment?.let { hide(it) }
        }
    }

    override fun toDialog(mediaId: MediaId, view: View) {
        TODO("Not yet implemented")
    }

}