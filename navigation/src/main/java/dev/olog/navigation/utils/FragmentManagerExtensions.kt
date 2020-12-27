package dev.olog.navigation.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.olog.navigation.BottomNavigator

fun FragmentManager.findFirstVisibleFragment(): Fragment? {
    var topFragment = this.topFragment
    if (topFragment == null) {
        topFragment = this.fragments
            .filter { it.isVisible }
            .firstOrNull { BottomNavigator.TAGS.contains(it.tag) }
    }
    if (topFragment == null) {
        Log.e("Navigator", "Something went wrong, for some reason no fragment was found")
    }
    return topFragment
}

// TODO move, search for all copies
val FragmentManager.topFragment: Fragment?
    get() {
        val topFragment = this.backStackEntryCount - 1
        if (topFragment > -1) {
            val tag = this.getBackStackEntryAt(topFragment).name
            return this.findFragmentByTag(tag)
        }
        return null
    }