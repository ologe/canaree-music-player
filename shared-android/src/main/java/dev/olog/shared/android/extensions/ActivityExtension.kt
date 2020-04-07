package dev.olog.shared.android.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

inline fun FragmentActivity.alertDialog(crossinline builder: MaterialAlertDialogBuilder.() -> MaterialAlertDialogBuilder) {
    MaterialAlertDialogBuilder(this)
        .builder()
        .show()
}