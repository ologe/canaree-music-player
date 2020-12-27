package dev.olog.navigation.internal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import dev.olog.navigation.BottomNavigationPage
import dev.olog.navigation.BottomNavigator
import dev.olog.navigation.R
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.navigation.utils.topFragment
import javax.inject.Inject
import javax.inject.Provider

internal class BottomNavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : BaseNavigator(),
    BottomNavigator {

    override fun bottomNavigate(page: BottomNavigationPage) {
        val activity = activityProvider() ?: return

        clearBackStack(activity)

        val screen = page.toScreen()

        val tag = screen.tag

        activity.supportFragmentManager.commit {
            disallowAddToBackStack()
            setReorderingAllowed(true)
            // hide other categories fragment
            hidesAllBottomNavigationFragments(activity)

            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            val existingFragment = activity.supportFragmentManager.findFragmentByTag(tag)
            if (existingFragment == null) {
                val fragment = fragments[screen]!!.get()
                add(R.id.fragmentContainer, fragment, tag)
            } else {
                show(existingFragment)
            }
        }
    }

    private fun clearBackStack(activity: FragmentActivity) {
        for (index in 0..activity.supportFragmentManager.backStackEntryCount) {
            activity.supportFragmentManager.popBackStack()
        }
    }

    private fun FragmentTransaction.hidesAllBottomNavigationFragments(activity: FragmentActivity){
        activity.supportFragmentManager.fragments
            .asSequence()
            .filter { BottomNavigator.TAGS.contains(it.tag) }
            .forEach { hide(it) }
    }

    private fun BottomNavigationPage.toScreen(): FragmentScreen {
        return when (this){
            BottomNavigationPage.LIBRARY_TRACKS -> FragmentScreen.LIBRARY_TRACKS
            BottomNavigationPage.LIBRARY_PODCASTS -> FragmentScreen.LIBRARY_PODCASTS
            BottomNavigationPage.SEARCH -> FragmentScreen.SEARCH
            BottomNavigationPage.QUEUE -> FragmentScreen.QUEUE
        }
    }

}