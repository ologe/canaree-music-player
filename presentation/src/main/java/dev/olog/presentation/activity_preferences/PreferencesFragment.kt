package dev.olog.presentation.activity_preferences

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import dev.olog.presentation.R
import dev.olog.shared_android.Constants
import dev.olog.shared_android.CoverUtils

class PreferencesFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)
    }


    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == activity.getString(R.string.prefs_icon_color_key)){
            val isDark = sharedPreferences.getBoolean(key, false)
            CoverUtils.isIconDark = isDark
            activity!!.setResult(Activity.RESULT_OK)
        }
        if (key == activity.getString(R.string.prefs_quick_action_key)){
            Constants.updateQuickAction(activity)
            activity!!.setResult(Activity.RESULT_OK)
        }
    }

}