package dev.olog.presentation.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

@Deprecated("delete")
fun allowed(): Boolean = false

@Deprecated("delete")
fun superCerealTransition(
    activity: FragmentActivity,
    fragment: Fragment,
    tag: String,
    transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE
) {
    if (!allowed()) {
        return
    }
}