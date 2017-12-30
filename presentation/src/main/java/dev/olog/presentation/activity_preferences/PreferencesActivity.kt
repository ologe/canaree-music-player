package dev.olog.presentation.activity_preferences

import android.os.Bundle
import android.preference.PreferenceActivity
import dev.olog.presentation.R

class PreferencesActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
    }

}