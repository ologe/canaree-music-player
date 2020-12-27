package dev.olog.presentation.navigator

import android.util.Log
import androidx.fragment.app.*
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.presentation.R
import dev.olog.presentation.queue.PlayingQueueFragment
import dev.olog.presentation.search.SearchFragment
import dev.olog.shared.android.extensions.getTopFragment

const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

// fragment tag, last added
var backStackCount = mutableMapOf<String, Int>()

private var lastRequest: Long = -1

private val basicFragments = listOf(
    FragmentScreen.LIBRARY_TRACKS.tag,
    FragmentScreen.LIBRARY_PODCASTS.tag,
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

@Deprecated("")
fun findFirstVisibleFragment(fragmentManager: FragmentManager): Fragment? {
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

fun superCerealTransition(
    activity: FragmentActivity,
    fragment: Fragment,
    tag: String,
    transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE
) {
    if (!allowed()) {
        return
    }

    val topFragment = findFirstVisibleFragment(activity.supportFragmentManager)

    activity.supportFragmentManager.commit {
        setReorderingAllowed(true)
        setTransition(transition)
        topFragment?.let { hide(it) }
        add(R.id.fragmentContainer, fragment, tag)
        addToBackStack(tag)
    }
}