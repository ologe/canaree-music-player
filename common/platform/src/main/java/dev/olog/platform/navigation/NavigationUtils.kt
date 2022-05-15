package dev.olog.platform.navigation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.R
import dev.olog.platform.containsTag
import dev.olog.shared.extension.getTopFragment

const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

private var lastRequest: Long = -1

fun allowed(): Boolean {
    val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
    lastRequest = System.currentTimeMillis()
    return allowed
}

fun findFirstVisibleFragment(
    fragmentManager: FragmentManager,
    tags: Set<BottomNavigationFragmentTag>,
): Fragment? {
    var topFragment = fragmentManager.getTopFragment()
    if (topFragment == null) {
        topFragment = fragmentManager.fragments
            .filter { it.isVisible }
            .firstOrNull { tags.containsTag(it.tag) }
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
    tags: Set<BottomNavigationFragmentTag>,
    transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE
) {
    if (!allowed()) {
        return
    }

    val topFragment = findFirstVisibleFragment(activity.supportFragmentManager, tags)

    activity.supportFragmentManager.commit {
        setReorderingAllowed(true)
        setTransition(transition)
        topFragment?.let { hide(it) }
        add( // tag is always needed, can't be null, used by ScrollHelper
            R.id.fragmentContainer,
            fragment,
            tag
        )
        addToBackStack(tag)
    }
}