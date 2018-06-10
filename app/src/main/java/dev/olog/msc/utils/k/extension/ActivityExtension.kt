package dev.olog.msc.utils.k.extension

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.view.View
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.utils.isP

fun FragmentActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}

fun FragmentActivity.getTopFragment(): Fragment? {
    val topFragment = supportFragmentManager.backStackEntryCount - 1
    if (topFragment > -1){
        val tag = supportFragmentManager.getBackStackEntryAt(topFragment).name
        return supportFragmentManager.findFragmentByTag(tag)
    }
    return null
}

@SuppressLint("NewApi")
fun View.hasNotch(): Boolean {
    if (isP()){
        val displayCutout = rootWindowInsets.displayCutout
        return displayCutout != null
    }
    return false
}

fun FragmentActivity.simpleDialog(builder: AlertDialog.Builder.() -> AlertDialog.Builder){
    ThemedDialog.builder(this)
            .builder()
            .show()
}