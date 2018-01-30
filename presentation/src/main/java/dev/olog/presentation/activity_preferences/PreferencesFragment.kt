package dev.olog.presentation.activity_preferences

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivity
import dev.olog.presentation.activity_preferences.blacklist.BlacklistFragment
import dev.olog.presentation.activity_preferences.categories.LibraryCategoriesFragment
import dev.olog.shared_android.Constants
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.RootUtils
import org.jetbrains.anko.toast

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var libraryCategories : Preference
    private lateinit var blacklist : Preference
    private lateinit var neuralStyle: Preference
    private lateinit var usedEqualizer: SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(R.string.prefs_library_categories_key))
        blacklist = preferenceScreen.findPreference(getString(R.string.prefs_blacklist_key))
        neuralStyle = preferenceScreen.findPreference(getString(R.string.prefs_neural_network_style_key))
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
            BlacklistFragment.newInstance().show(activity!!.supportFragmentManager,
                    BlacklistFragment.TAG)
            true
        }
        neuralStyle.setOnPreferenceClickListener {
            val intent = Intent(activity!!, NeuralNetworkActivity::class.java)
            activity!!.startActivity(intent)
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
        neuralStyle.onPreferenceClickListener = null
        usedEqualizer.onPreferenceClickListener = null
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
        if (key == getString(dev.olog.shared_android.R.string.prefs_use_neural_images_key)){
            Constants.useNeuralImages = sharedPreferences.getBoolean(key, false)
            requestMainActivityToRecreate()
        }
    }

    private fun requestMainActivityToRecreate(){
        activity!!.setResult(Activity.RESULT_OK)
    }

}