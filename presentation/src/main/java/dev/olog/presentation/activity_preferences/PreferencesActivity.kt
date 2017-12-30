package dev.olog.presentation.activity_preferences

import android.os.Bundle
import android.preference.PreferenceActivity
import dev.olog.presentation.R
import dev.olog.presentation.utils.extension.setLightStatusBar
import kotlinx.android.synthetic.main.activity_preferences.*

class PreferencesActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
        setContentView(R.layout.activity_preferences)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}