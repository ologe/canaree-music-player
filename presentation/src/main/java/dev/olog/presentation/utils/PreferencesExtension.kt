package dev.olog.presentation.utils

import androidx.preference.Preference
import androidx.preference.PreferenceGroup

internal fun PreferenceGroup.forEach(action: (Preference) -> Unit) {
    val size = preferenceCount
    for (index in 0 until size) {
        val preference = getPreference(index)
        if (preference is PreferenceGroup) {
            preference.forEach(action)
        } else {
            action(preference)
        }
    }
}