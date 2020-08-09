package dev.olog.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.core.extensions.getTopFragment
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterAnimation
import dev.olog.navigation.transition.setupExitAnimation
import dev.olog.navigation.utils.ActivityProvider
import javax.inject.Inject
import javax.inject.Provider

internal class BottomNavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>
): BaseNavigator(), BottomNavigator {

    override fun bottomNavigate(screen: FragmentScreen) {
        val activity = activityProvider() ?: return
        val topFragment = activity.supportFragmentManager.getTopFragment()

        when {
            topFragment != null -> fromOtherPages(activity, topFragment, screen)
            else -> {
                val current = BottomNavigator.TAGS
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

        replaceFragment(activity, fragments[screen]?.get(), screen.tag) { fragment ->
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
        topFragment?.setupExitAnimation(activity)

        replaceFragment(activity, fragments[screen]?.get(), screen.tag) { fragment ->
            fragment.setupEnterAnimation(activity)
        }
    }

}