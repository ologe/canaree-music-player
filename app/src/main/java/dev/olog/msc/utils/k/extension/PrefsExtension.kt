package dev.olog.msc.utils.k.extension

import android.content.SharedPreferences

fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> SharedPreferences.Editor){
    edit().func().apply()
}