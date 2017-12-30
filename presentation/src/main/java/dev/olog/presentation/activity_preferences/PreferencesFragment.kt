package dev.olog.presentation.activity_preferences

import android.os.Bundle
import android.preference.PreferenceFragment
import dev.olog.presentation.R

class PreferencesFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)
    }

}