package dev.olog.msc.presentation.preferences

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.utils.k.extension.*

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var libraryCategories : Preference
    private lateinit var blacklist : Preference
    private lateinit var iconShape : Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(R.string.prefs_library_categories_key))
        blacklist = preferenceScreen.findPreference(getString(R.string.prefs_blacklist_key))
        iconShape = preferenceScreen.findPreference(getString(R.string.prefs_icon_shape_key))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIconShapeSummary()

        val billing = (act as PreferencesActivity).billing
        billing.observeIsPremium()
                .take(2) // take current and after check values
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(this, { isPremium ->
                    forEach(preferenceScreen) { it.isEnabled = isPremium }

                    if (!isPremium) {
                        val v = act.window.decorView.findViewById<View>(android.R.id.content)
                        Snackbar.make(v, R.string.prefs_not_premium, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.prefs_not_premium_action, { billing.purchasePremium() })
                                .show()
                    }
                })
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
            act.fragmentTransaction {
                setReorderingAllowed(true)
                add(BlacklistFragment.newInstance(), BlacklistFragment.TAG)
            }
            true
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        libraryCategories.onPreferenceClickListener = null
        blacklist.onPreferenceClickListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.prefs_quick_action_key)){
            AppConstants.updateQuickAction(act)
            requestMainActivityToRecreate()
        }
        if (key == getString(R.string.prefs_icon_shape_key)){
            AppConstants.updateIconShape(act)
            setIconShapeSummary()
            requestMainActivityToRecreate()
        }
    }

    private fun setIconShapeSummary(){
        val value = preferenceManager.sharedPreferences.getString(
                ctx.getString(R.string.prefs_icon_shape_key), ctx.getString(R.string.prefs_icon_shape_rounded))
        iconShape.summary = when (value){
            ctx.getString(R.string.prefs_icon_shape_square) -> ctx.getString(R.string.common_shape_square)
            ctx.getString(R.string.prefs_icon_shape_rounded) -> ctx.getString(R.string.common_shape_rounded)
            else -> ""
        }
    }

    private fun requestMainActivityToRecreate(){
        act.setResult(Activity.RESULT_OK)
    }

}