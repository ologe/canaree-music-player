package dev.olog.presentation.prefs

import androidx.preference.Preference
import androidx.preference.PreferenceGroup

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