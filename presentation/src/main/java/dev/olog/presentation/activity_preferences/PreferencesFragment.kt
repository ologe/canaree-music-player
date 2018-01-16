package dev.olog.presentation.activity_preferences

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import dev.olog.presentation.R
import dev.olog.presentation.activity_preferences.blacklist.BlacklistFragment
import dev.olog.presentation.activity_preferences.categories.LibraryCategoriesFragment
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragment
import dev.olog.presentation.utils.extension.transaction
import dev.olog.shared_android.Constants
import dev.olog.shared_android.CoverUtils

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var libraryCategories : Preference
    private lateinit var blacklist : Preference
    private lateinit var neural: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(R.string.prefs_library_categories_key))
        blacklist = preferenceScreen.findPreference(getString(R.string.prefs_blacklist_key))
        neural = preferenceScreen.findPreference("neural")
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
            BlacklistFragment.newInstance().show(activity!!.supportFragmentManager,
                    BlacklistFragment.TAG)
            true
        }
        neural.setOnPreferenceClickListener {
            activity!!.supportFragmentManager.transaction {
                add(android.R.id.content, NeuralNetworkFragment.newInstance())
                addToBackStack(NeuralNetworkFragment.TAG)
            }
            true
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        libraryCategories.onPreferenceClickListener = null
        blacklist.onPreferenceClickListener = null
        neural.onPreferenceClickListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == activity!!.getString(R.string.prefs_icon_color_key)){
            val isDark = sharedPreferences.getBoolean(key, true)
            CoverUtils.isIconDark = isDark
            requestMainActivityToRecreate()
        }
        if (key == activity!!.getString(R.string.prefs_quick_action_key)){
            Constants.updateQuickAction(activity!!)
            requestMainActivityToRecreate()
        }
        if (key == "use_stylized_images"){
            Constants.useNeuralImages = sharedPreferences.getBoolean(key, false)
            requestMainActivityToRecreate()
        }
    }

    private fun requestMainActivityToRecreate(){
        activity!!.setResult(Activity.RESULT_OK)
    }

}