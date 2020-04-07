package dev.olog.core.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.getTopFragment(): Fragment? {
    val topFragment = this.backStackEntryCount - 1
    if (topFragment > -1) {
        val tag = this.getBackStackEntryAt(topFragment).name
        val fragment = this.findFragmentByTag(tag)
        if (fragment?.isVisible == true) {
            return fragment
        }
    }
    return null
}