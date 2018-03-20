package dev.olog.msc.utils.k.extension

import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceGroup

fun forEach(group: PreferenceGroup, action: (Preference) -> Unit){
    val size = group.preferenceCount
    for(index in 0 until size){
        val preference = group.getPreference(index)
        if (preference is PreferenceGroup){
            forEach(preference, action)
        } else {
            action(preference)
        }
    }
}