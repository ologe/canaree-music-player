package dev.olog.navigation.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.olog.core.extensions.getTopFragment
import dev.olog.navigation.BottomNavigator
import timber.log.Timber

fun FragmentManager.findFirstVisibleFragment(): Fragment? {
    var topFragment = getTopFragment()
    if (topFragment == null) {
        topFragment = fragments
            .filter { it.isVisible }
            .firstOrNull { BottomNavigator.TAGS.contains(it.tag) }
    }
    if (topFragment == null) {
        Timber.e("Navigator: Something went wrong, for some reason no fragment was found")
    }
    return topFragment
}