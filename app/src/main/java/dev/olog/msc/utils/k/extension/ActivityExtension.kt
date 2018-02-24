package dev.olog.msc.utils.k.extension

import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun FragmentActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}

fun FragmentManager.searchInFragmentBackStack(tag: String): Boolean{
    if (backStackEntryCount < 1){
        return false
    }

    return (0 until backStackEntryCount)
            .map { getBackStackEntryAt(it) }
            .filter { it.name == tag }
            .map { findFragmentByTag(it.name) }
            .any { it != null }
}