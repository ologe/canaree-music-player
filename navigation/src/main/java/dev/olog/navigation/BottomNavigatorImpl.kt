package dev.olog.navigation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.transition.MaterialSharedAxis
import dev.olog.core.extensions.getTopFragment
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterAnimation
import dev.olog.navigation.transition.setupExitAnimation
import javax.inject.Inject
import javax.inject.Provider

internal class BottomNavigatorImpl @Inject constructor(
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>
): BaseNavigator(), BottomNavigator {

    private val tags = listOf(
        FragmentScreen.LIBRARY_TRACKS.tag,
        FragmentScreen.LIBRARY_PODCAST.tag,
        FragmentScreen.SEARCH.tag,
        FragmentScreen.QUEUE.tag
    )

    override fun bottomNavigate(
        activity: FragmentActivity,
        screen: FragmentScreen
    ) {
        val topFragment = activity.supportFragmentManager.getTopFragment()

        when {
            topFragment != null -> fromOtherPages(activity, topFragment, screen)
            else -> {
                val current = tags
                    .mapNotNull { activity.supportFragmentManager.findFragmentByTag(it) }
                    .find { it.isVisible }
                fromAnotherBottomNavigationPage(activity, current, screen)
            }
        }
    }

    private fun fromOtherPages(
        activity: FragmentActivity,
        topFragment: Fragment,
        screen: FragmentScreen
    ) {
        topFragment.setupExitAnimation(activity)

        replaceFragment(activity, fragments[screen]?.get(), screen.tag, forced = true) { fragment ->
            fragment.setupEnterAnimation(activity)
        }
    }

    private fun fromAnotherBottomNavigationPage(
        activity: FragmentActivity,
        topFragment: Fragment?,
        screen: FragmentScreen
    ) {
        if (topFragment?.tag == screen.tag) {
            // don't reopen same page
            return
        }
        topFragment?.let { setupExitAnimation(activity, it, screen.tag) }

        replaceFragment(activity, fragments[screen]?.get(), screen.tag, forced = true) { fragment ->
            topFragment?.let { setupEnterAnimation(activity, it, fragment, screen.tag) }
        }
    }

    private fun setupEnterAnimation(
        context: Context,
        current: Fragment,
        new: Fragment,
        newTag: String
    ) {
        val libraryTrack = FragmentScreen.LIBRARY_TRACKS.tag
        val libraryPodcast = FragmentScreen.LIBRARY_PODCAST.tag
        if (current.tag == libraryTrack && newTag == libraryPodcast) {
            new.enterTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, true)
        } else if (current.tag == libraryPodcast && newTag == libraryTrack) {
            new.enterTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, false)
        } else {
            new.setupEnterAnimation(context)
        }
    }

    private fun setupExitAnimation(
        context: Context,
        current: Fragment,
        newTag: String
    ) {
        val libraryTrack = FragmentScreen.LIBRARY_TRACKS.tag
        val libraryPodcast = FragmentScreen.LIBRARY_PODCAST.tag
        if (current.tag == libraryTrack && newTag == libraryPodcast) {
            current.exitTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, true)
        } else if (current.tag == libraryPodcast && newTag == libraryTrack) {
            current.exitTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, false)
        } else {
            current.setupExitAnimation(context)
        }
    }

}