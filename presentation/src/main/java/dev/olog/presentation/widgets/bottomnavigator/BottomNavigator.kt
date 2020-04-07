package dev.olog.presentation.widgets.bottomnavigator

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialSharedAxis
import dev.olog.analytics.TrackerFacade
import dev.olog.presentation.R
import dev.olog.presentation.animations.setupEnterAnimation
import dev.olog.presentation.animations.setupExitAnimation
import dev.olog.presentation.library.LibraryFragment
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.LibraryPage
import dev.olog.presentation.queue.PlayingQueueFragment
import dev.olog.presentation.search.SearchFragment
import dev.olog.core.extensions.getTopFragment

internal class BottomNavigator {

    private val tags = listOf(
        LibraryFragment.TAG_TRACK,
        LibraryFragment.TAG_PODCAST,
        SearchFragment.TAG,
        PlayingQueueFragment.TAG
    )

    fun navigate(
        activity: FragmentActivity,
        trackerFacade: TrackerFacade,
        page: BottomNavigationPage,
        libraryPage: LibraryPage
    ) {
        val newTag = page.toFragmentTag(libraryPage)

        val topFragment = activity.supportFragmentManager.getTopFragment()
        when {
            topFragment != null -> fromOtherPages(activity, topFragment, newTag, trackerFacade)
            else -> {
                val current = tags
                    .mapNotNull { activity.supportFragmentManager.findFragmentByTag(it) }
                    .find { it.isVisible }
                fromAnotherBottomNavigationPage(activity, current, newTag, trackerFacade)
            }
        }
    }

    private fun fromAnotherBottomNavigationPage(
        activity: FragmentActivity,
        topFragment: Fragment?,
        newTag: String,
        trackerFacade: TrackerFacade
    ) {
        if (topFragment?.tag == newTag) {
            // don't reopen same page
            return
        }
        topFragment?.let { setupExitAnimation(activity, it, newTag) }

        activity.supportFragmentManager.commit {
            val newInstance = tagToInstance(newTag)
            trackerFacade.trackScreen(newInstance::class.java.simpleName, newInstance.arguments)
            topFragment?.let { setupEnterAnimation(activity, it, newInstance, newTag) }
            replace(R.id.fragmentContainer, newInstance, newTag)
        }
    }

    private fun fromOtherPages(
        activity: FragmentActivity,
        topFragment: Fragment,
        newTag: String,
        trackerFacade: TrackerFacade
    ) {
        topFragment.setupExitAnimation(activity)

        activity.supportFragmentManager.commit {
            val newInstance = tagToInstance(newTag)
            trackerFacade.trackScreen(newInstance::class.java.simpleName, newInstance.arguments)
            newInstance.setupEnterAnimation(activity)
            replace(R.id.fragmentContainer, newInstance, newTag)
        }
    }

    private fun setupEnterAnimation(
        context: Context,
        current: Fragment,
        new: Fragment,
        newTag: String
    ) {
        if (current.tag == LibraryFragment.TAG_TRACK && newTag == LibraryFragment.TAG_PODCAST) {
            new.enterTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, true)
        } else if (current.tag == LibraryFragment.TAG_PODCAST && newTag == LibraryFragment.TAG_TRACK) {
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
        if (current.tag == LibraryFragment.TAG_TRACK && newTag == LibraryFragment.TAG_PODCAST) {
            current.exitTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, true)
        } else if (current.tag == LibraryFragment.TAG_PODCAST && newTag == LibraryFragment.TAG_TRACK) {
            current.exitTransition = MaterialSharedAxis.create(context, MaterialSharedAxis.X, false)
        } else {
            current.setupExitAnimation(context)
        }
    }

    private fun BottomNavigationPage.toFragmentTag(libraryPage: LibraryPage): String {
        return when (this){
            BottomNavigationPage.LIBRARY -> {
                when(libraryPage){
                    LibraryPage.TRACKS -> LibraryFragment.TAG_TRACK
                    LibraryPage.PODCASTS -> LibraryFragment.TAG_PODCAST
                }
            }
            BottomNavigationPage.SEARCH -> SearchFragment.TAG
            BottomNavigationPage.QUEUE -> PlayingQueueFragment.TAG
        }
    }

    private fun tagToInstance(tag: String): Fragment = when (tag) {
        LibraryFragment.TAG_TRACK -> LibraryFragment.newInstance(false)
        LibraryFragment.TAG_PODCAST -> LibraryFragment.newInstance(true)
        SearchFragment.TAG -> SearchFragment()
        PlayingQueueFragment.TAG -> PlayingQueueFragment()
        else -> throw IllegalArgumentException("invalid fragment tag $tag")
    }
}