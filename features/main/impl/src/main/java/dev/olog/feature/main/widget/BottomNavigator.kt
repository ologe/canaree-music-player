package dev.olog.feature.main.widget

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import dev.olog.feature.library.api.FeatureLibraryNavigator
import dev.olog.feature.main.R
import dev.olog.feature.main.api.BottomNavigationPage
import dev.olog.feature.library.api.LibraryPage
import dev.olog.feature.queue.api.FeatureQueueNavigator
import dev.olog.feature.search.api.FeatureSearchNavigator
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.containsTag
import javax.inject.Inject

class BottomNavigator @Inject constructor(
    private val tags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
    private val featureLibraryNavigator: FeatureLibraryNavigator,
    private val featureSearchNavigator: FeatureSearchNavigator,
    private val featureQueueNavigator: FeatureQueueNavigator,
) {

    fun navigate(
        activity: FragmentActivity,
        page: BottomNavigationPage,
        libraryPage: LibraryPage
    ) {
        val fragmentTag = page.toFragmentTag(libraryPage)

        if (!tags.containsTag(fragmentTag)) {
            throw IllegalArgumentException("invalid fragment tag $fragmentTag")
        }

        for (index in 0..activity.supportFragmentManager.backStackEntryCount) {
            // clear the backstack
            activity.supportFragmentManager.popBackStack()
        }

        activity.supportFragmentManager.commit {
            disallowAddToBackStack()
            setReorderingAllowed(true)
            // hide other categories fragment
            hidesAllBottomNavigationFragments(activity)

            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment == null) {
                val newFragment = tagToInstance(fragmentTag)
                add(R.id.fragmentContainer, newFragment, fragmentTag)
            } else {
                show(fragment)
            }
        }
    }

    private fun FragmentTransaction.hidesAllBottomNavigationFragments(activity: FragmentActivity){
        activity.supportFragmentManager.fragments
                .asSequence()
                .filter { tags.containsTag(it.tag) }
                .forEach { hide(it) }
    }

    private fun BottomNavigationPage.toFragmentTag(libraryPage: LibraryPage): String {
        return when (this){
            BottomNavigationPage.LIBRARY -> {
                when(libraryPage){
                    LibraryPage.TRACKS -> featureLibraryNavigator.tracksFragmentTag()
                    LibraryPage.PODCASTS -> featureLibraryNavigator.podcastsFragmentTag()
                }
            }
            BottomNavigationPage.SEARCH -> featureSearchNavigator.searchFragmentTag()
            BottomNavigationPage.QUEUE -> featureQueueNavigator.queueFragmentTag()
        }
    }

    private fun tagToInstance(tag: String): Fragment = when (tag) {
        featureLibraryNavigator.tracksFragmentTag() -> featureLibraryNavigator.tracksFragment()
        featureLibraryNavigator.podcastsFragmentTag() -> featureLibraryNavigator.podcastsFragment()
        featureSearchNavigator.searchFragmentTag() -> featureSearchNavigator.searchFragment()
        featureQueueNavigator.queueFragmentTag() -> featureQueueNavigator.queueFragment()
        else -> throw IllegalArgumentException("invalid fragment tag $tag")
    }
}