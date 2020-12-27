package dev.olog.navigation.internal

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dev.olog.core.MediaId
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.navigation.destination.FragmentScreen
import javax.inject.Inject
import javax.inject.Provider

internal class NavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : BaseNavigator(),
    Navigator {

    override fun toFirstAccess() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.ONBOARDING]?.get()
        val tag = FragmentScreen.ONBOARDING.tag
        replaceFragment(activity, fragment, tag, android.R.id.content, forced = true)
    }

    override fun toDetailFragment(mediaId: MediaId) {
        val activity = activityProvider() ?: return
        // TODO collapse sliding panel here
        val fragment = fragments[FragmentScreen.DETAIL]?.get()
        val tag = createBackStackTag(FragmentScreen.DETAIL.tag)

        fragment?.arguments = bundleOf(
            Params.MEDIA_ID to mediaId.toString(),
        )

        val topFragment = findFirstVisibleFragment(activity.supportFragmentManager)
        addFragment(activity, fragment, tag) {
            addToBackStack(tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            topFragment?.let { hide(it) }
        }
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        TODO("Not yet implemented")
    }

    override fun toDialog(mediaId: MediaId, view: View) {
        TODO("Not yet implemented")
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        TODO("Not yet implemented")
    }

    // TODO move
    private val basicFragments = listOf<String>(
//        LibraryFragment.TAG_TRACK,
//        LibraryFragment.TAG_PODCAST,
//        SearchFragment.TAG,
//        PlayingQueueFragment.TAG
    )

    // TODO move
    fun findFirstVisibleFragment(fragmentManager: FragmentManager): Fragment? {
        var topFragment = fragmentManager.getTopFragment()
        if (topFragment == null) {
            topFragment = fragmentManager.fragments
                .filter { it.isVisible }
                .firstOrNull { basicFragments.contains(it.tag) }
        }
        if (topFragment == null) {
            Log.e("Navigator", "Something went wrong, for some reason no fragment was found")
        }
        return topFragment
    }

    // TODO move
    private fun FragmentManager.getTopFragment(): Fragment? {
        val topFragment = this.backStackEntryCount - 1
        if (topFragment > -1) {
            val tag = this.getBackStackEntryAt(topFragment).name
            return this.findFragmentByTag(tag)
        }
        return null
    }

}