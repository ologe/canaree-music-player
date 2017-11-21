package dev.olog.data.utils

import android.content.SharedPreferences

fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> SharedPreferences.Editor){
    edit().func().apply()
}