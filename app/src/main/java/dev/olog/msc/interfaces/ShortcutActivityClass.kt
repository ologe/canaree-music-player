package dev.olog.shared_android.interfaces

import android.support.v7.app.AppCompatActivity

interface ShortcutActivityClass {

    fun get(): Class<out AppCompatActivity>

}