package dev.olog.msc.utils.k.extension

import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction

fun FragmentActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}