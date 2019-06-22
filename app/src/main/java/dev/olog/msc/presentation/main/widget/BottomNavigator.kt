package dev.olog.msc.presentation.main.widget

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.msc.R

internal class BottomNavigator {

    private val tags = listOf(
            CategoriesFragment.TAG,
            CategoriesPodcastFragment.TAG,
            SearchFragment.TAG,
            PlayingQueueFragment.TAG
    )

    fun navigate(activity: FragmentActivity, page: CustomBottomNavigator.Page){
        val fragmentTag = page.toFragmentTag()

        if (!tags.contains(fragmentTag)) {
            throw IllegalArgumentException("invalid fragment tag $fragmentTag")
        }

        for (index in 0..activity.supportFragmentManager.backStackEntryCount) {
            // clear the backstack
            activity.supportFragmentManager.popBackStack()
        }

        activity.fragmentTransaction {
            disallowAddToBackStack()
            setReorderingAllowed(true)
            // hide other categories fragment
            hidesAllBottomNavigationFragments(activity)

            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment == null) {
                add(R.id.fragmentContainer, tagToInstance(fragmentTag), fragmentTag)
            } else {
                show(fragment)
            }
        }
    }

    private fun FragmentTransaction.hidesAllBottomNavigationFragments(activity: FragmentActivity){
        activity.supportFragmentManager.fragments
                .asSequence()
                .filter { tags.contains(it.tag) }
                .forEach { hide(it) }
    }

    private fun CustomBottomNavigator.Page.toFragmentTag(): String = when(this){
        CustomBottomNavigator.Page.SONGS -> CategoriesFragment.TAG
        CustomBottomNavigator.Page.PODCASTS -> CategoriesPodcastFragment.TAG
        CustomBottomNavigator.Page.SEARCH -> SearchFragment.TAG
        CustomBottomNavigator.Page.QUEUE -> PlayingQueueFragment.TAG
    }

    private fun tagToInstance(tag: String): Fragment = when (tag) {
        CategoriesFragment.TAG -> CategoriesFragment()
        CategoriesPodcastFragment.TAG -> CategoriesPodcastFragment()
        SearchFragment.TAG -> SearchFragment()
        PlayingQueueFragment.TAG -> PlayingQueueFragment()
        else -> throw IllegalArgumentException("invalid fragment tag $tag")
    }
}