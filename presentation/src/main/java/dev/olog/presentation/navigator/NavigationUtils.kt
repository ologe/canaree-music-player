package dev.olog.presentation.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.olog.presentation.library.LibraryFragment
import dev.olog.presentation.queue.PlayingQueueFragment
import dev.olog.presentation.search.SearchFragment
import dev.olog.core.extensions.getTopFragment
import timber.log.Timber

const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

// fragment tag, last added
var backStackCount = mutableMapOf<String, Int>()

private var lastRequest: Long = -1

private val basicFragments = listOf(
    LibraryFragment.TAG_TRACK,
    LibraryFragment.TAG_PODCAST,
    SearchFragment.TAG,
    PlayingQueueFragment.TAG
)

/**
 * Use this when you can instantiate multiple times same fragment
 */
fun createBackStackTag(fragmentTag: String): String {
    // get last + 1
    val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
    // update
    backStackCount[fragmentTag] = counter
    // creates new
    return "$fragmentTag$counter"
}

fun allowed(): Boolean {
    val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
    lastRequest = System.currentTimeMillis()
    return allowed
}

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