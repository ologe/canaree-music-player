package dev.olog.msc.utils.k.extension

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.View
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
        // todo activated notch detection on release
//        val displayCutout = rootWindowInsets.displayCutout
//        return displayCutout != null
    }
    return false
}