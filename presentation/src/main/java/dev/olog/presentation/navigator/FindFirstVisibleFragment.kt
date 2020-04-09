package dev.olog.presentation.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.olog.core.extensions.getTopFragment
import dev.olog.presentation.queue.PlayingQueueFragment
import dev.olog.feature.search.SearchFragment
import timber.log.Timber

private val basicFragments = listOf(
//    LibraryFragment.TAG_TRACK, TODO
//    LibraryFragment.TAG_PODCAST,
    SearchFragment.TAG,
    PlayingQueueFragment.TAG
)

fun findFirstVisibleFragment(fragmentManager: FragmentManager): Fragment? {
    var topFragment = fragmentManager.getTopFragment()
    if (topFragment == null) {
        topFragment = fragmentManager.fragments
            .filter { it.isVisible }
            .firstOrNull { basicFragments.contains(it.tag) }
    }
    if (topFragment == null) {
        Timber.e("Navigator: Something went wrong, for some reason no fragment was found")
    }
    return topFragment
}