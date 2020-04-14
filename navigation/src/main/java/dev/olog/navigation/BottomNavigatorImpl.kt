package dev.olog.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.core.extensions.getTopFragment
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterFadeAnimation
import dev.olog.navigation.transition.setupExitFadeAnimation
import javax.inject.Inject
import javax.inject.Provider

internal class BottomNavigatorImpl @Inject constructor(
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>
): BaseNavigator(), BottomNavigator {

    private val tags = listOf(
        FragmentScreen.HOME.tag,
        FragmentScreen.TRACKS.tag,
        FragmentScreen.SEARCH.tag,
        FragmentScreen.PLAYLISTS.tag,
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
        topFragment.setupExitFadeAnimation(activity)

        replaceFragment(activity, fragments[screen]?.get(), screen.tag, forced = true) { fragment ->
            fragment.setupEnterFadeAnimation(activity)
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
        topFragment?.setupExitFadeAnimation(activity)

        replaceFragment(activity, fragments[screen]?.get(), screen.tag, forced = true) { fragment ->
            topFragment?.let { fragment.setupEnterFadeAnimation(activity) }
        }
    }

}