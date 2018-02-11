package dev.olog.msc.utils.k.extension

import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}