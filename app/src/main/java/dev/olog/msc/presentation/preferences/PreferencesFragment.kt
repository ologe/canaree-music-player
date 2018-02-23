package dev.olog.msc.presentation.preferences

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.utils.RootUtils
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.msc.utils.k.extension.toast

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var libraryCategories : Preference
    private lateinit var blacklist : Preference
    private lateinit var usedEqualizer: SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(R.string.prefs_library_categories_key))
        blacklist = preferenceScreen.findPreference(getString(R.string.prefs_blacklist_key))
        usedEqualizer = preferenceScreen.findPreference(getString(R.string.prefs_used_equalizer_key)) as SwitchPreference
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (RootUtils.isDeviceRooted()){
            usedEqualizer.isChecked = false
            preferenceScreen.sharedPreferences.edit()
                    .putBoolean(getString(R.string.prefs_used_equalizer_key), false).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        libraryCategories.setOnPreferenceClickListener {
            LibraryCategoriesFragment.newInstance().show(activity!!.supportFragmentManager,
                    LibraryCategoriesFragment.TAG)
            true
        }
        blacklist.setOnPreferenceClickListener {
            activity!!.fragmentTransaction {
                add(BlacklistFragment.newInstance(), BlacklistFragment.TAG)
                setReorderingAllowed(true)
            }
            true
        }
        usedEqualizer.setOnPreferenceClickListener {
            if (RootUtils.isDeviceRooted()){
                activity!!.toast(R.string.prefs_used_equalizer_not_found)
                usedEqualizer.isChecked = false
            }
            false
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        libraryCategories.onPreferenceClickListener = null
        blacklist.onPreferenceClickListener = null
        usedEqualizer.onPreferenceClickListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == activity!!.getString(R.string.prefs_quick_action_key)){
            AppConstants.updateQuickAction(activity!!)
            requestMainActivityToRecreate()
        }
    }

    private fun requestMainActivityToRecreate(){
        activity!!.setResult(Activity.RESULT_OK)
    }

}