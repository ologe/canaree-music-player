package dev.olog.shared_android.interfaces

import android.support.v7.app.AppCompatActivity

interface MainActivityClass {

    fun get(): Class<out AppCompatActivity>

}