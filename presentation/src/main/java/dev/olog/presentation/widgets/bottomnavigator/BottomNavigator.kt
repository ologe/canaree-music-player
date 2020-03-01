package dev.olog.presentation.widgets.bottomnavigator

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import dev.olog.shared.android.extensions.fragmentTransaction

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
        val fragmentTag = page.toFragmentTag(libraryPage)

        val current = tags.mapNotNull { activity.supportFragmentManager.findFragmentByTag(it) }
            .find { it.isVisible }


        current?.let { setupExitAnimation(activity, it, fragmentTag) }

        activity.fragmentTransaction {
            current?.let { hide(it) }

            val toAdd = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (toAdd != null) {
                trackerFacade.trackScreen(toAdd::class.java.simpleName, toAdd.arguments)

                setupEnterAnimation(activity, current!!, toAdd, fragmentTag)
                show(toAdd)
            } else {
                val newInstance = tagToInstance(fragmentTag)
                trackerFacade.trackScreen(newInstance::class.java.simpleName, newInstance.arguments)

                current?.let {
                    setupEnterAnimation(activity, it, newInstance, fragmentTag)
                }

                replace(R.id.fragmentContainer, newInstance, fragmentTag)
            }
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