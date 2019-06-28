package dev.olog.msc.presentation.navigator

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.R
import dev.olog.presentation.queue.PlayingQueueFragment
import dev.olog.presentation.search.SearchFragment
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.msc.utils.k.extension.getTopFragment
import dev.olog.presentation.library.LibraryFragment

internal const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

// fragment tag, last added
internal var backStackCount = mutableMapOf<String, Int>()

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
internal fun createBackStackTag(fragmentTag: String): String {
    // get last + 1
    val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
    // update
    backStackCount[fragmentTag] = counter
    // creates new
    return "$fragmentTag$counter"
}

internal fun allowed(): Boolean {
    val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
    lastRequest = System.currentTimeMillis()
    return allowed
}

internal fun findFirstVisibleFragment(fragmentManager: FragmentManager): Fragment? {
    var topFragment = fragmentManager.getTopFragment()
    if (topFragment == null) {
        topFragment = fragmentManager.fragments
            .filter { it.isVisible }
            .firstOrNull { basicFragments.contains(it.tag) }
    }
    if (topFragment == null) {
        Log.e("Navigator", "Something went wrong, for some reason no fragment was found")
    }
    return topFragment
}

internal fun superCerealTransition(activity: FragmentActivity, fragment: Fragment, tag: String) {
    if (!allowed()) {
        return
    }

    val topFragment = findFirstVisibleFragment(activity.supportFragmentManager)

    activity.fragmentTransaction {
        setReorderingAllowed(true)
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        topFragment?.let { hide(it) }
        add(
            R.id.fragmentContainer,
            fragment,
            tag
        )
        addToBackStack(tag)
    }
}