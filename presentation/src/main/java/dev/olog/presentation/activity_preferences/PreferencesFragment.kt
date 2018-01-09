package dev.olog.presentation.activity_preferences

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import dev.olog.presentation.R
import dev.olog.presentation.activity_preferences.categories.LibraryCategoriesFragment
import dev.olog.shared_android.Constants
import dev.olog.shared_android.CoverUtils

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var libraryCategories : Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(R.string.prefs_library_categories_key)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        libraryCategories.setOnPreferenceClickListener {
            LibraryCategoriesFragment.newInstance().show(activity!!.supportFragmentManager,
                    LibraryCategoriesFragment.TAG)
            true
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        libraryCategories.onPreferenceClickListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == activity!!.getString(R.string.prefs_icon_color_key)){
            val isDark = sharedPreferences.getBoolean(key, false)
            CoverUtils.isIconDark = isDark
            activity!!.setResult(Activity.RESULT_OK)
        }
        if (key == activity!!.getString(R.string.prefs_quick_action_key)){
            Constants.updateQuickAction(activity!!)
            activity!!.setResult(Activity.RESULT_OK)
        }
    }

}