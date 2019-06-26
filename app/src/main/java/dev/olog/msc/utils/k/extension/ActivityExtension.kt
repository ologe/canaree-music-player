package dev.olog.msc.utils.k.extension

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.presentation.theme.ThemedDialog

fun FragmentActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}

fun FragmentManager.getTopFragment(): Fragment? {
    val topFragment = this.backStackEntryCount - 1
    if (topFragment > -1) {
        val tag = this.getBackStackEntryAt(topFragment).name
        return this.findFragmentByTag(tag)
    }
    return null
}

fun FragmentActivity.simpleDialog(builder: AlertDialog.Builder.() -> AlertDialog.Builder){
    ThemedDialog.builder(this)
            .builder()
            .show()
}