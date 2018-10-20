package dev.olog.msc.presentation.preferences

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.interactor.prefs.TutorialPreferenceUseCase
import dev.olog.msc.isLowMemoryDevice
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.presentation.preferences.last.fm.credentials.LastFmCredentialsFragment
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.presentation.utils.ColorPalette
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject lateinit var tutorialPrefsUseCase: TutorialPreferenceUseCase

    private lateinit var libraryCategories : Preference
    private lateinit var podcastCategories : Preference
    private lateinit var blacklist : Preference
    private lateinit var iconShape : Preference
    private lateinit var deleteCache : Preference
    private lateinit var lastFmCredentials: Preference
    private lateinit var autoCreateImages: SwitchPreference
    private lateinit var accentColorChooser: Preference
    private lateinit var resetTutorial: Preference

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(R.string.prefs_library_categories_key))
        podcastCategories = preferenceScreen.findPreference(getString(R.string.prefs_podcast_library_categories_key))
        blacklist = preferenceScreen.findPreference(getString(R.string.prefs_blacklist_key))
        iconShape = preferenceScreen.findPreference(getString(R.string.prefs_icon_shape_key))
        deleteCache = preferenceScreen.findPreference(getString(R.string.prefs_delete_cached_images_key))
        lastFmCredentials = preferenceScreen.findPreference(getString(R.string.prefs_last_fm_credentials_key))
        autoCreateImages = preferenceScreen.findPreference(getString(R.string.prefs_auto_create_images_key)) as SwitchPreference
        accentColorChooser = preferenceScreen.findPreference(getString(R.string.prefs_accent_color_key))
        resetTutorial = preferenceScreen.findPreference(getString(R.string.prefs_reset_tutorial_key))
    }

    private var needsToRecreate = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val billing = (act as PreferencesActivity).billing
        billing.observeIsPremium()
                .take(2) // take current and after check values
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(viewLifecycleOwner) { isPremium ->
                    forEach(preferenceScreen) { it.isEnabled = isPremium }

                    if (!isPremium) {
                        val v = act.window.decorView.findViewById<View>(android.R.id.content)
                        Snackbar.make(v, R.string.prefs_not_premium, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.prefs_not_premium_action) { billing.purchasePremium() }
                                .show()
                    }
                }

        val lowRam = isLowMemoryDevice(ctx)
        if (lowRam){
            autoCreateImages.isChecked = false
            autoCreateImages.isEnabled = false
            autoCreateImages.summaryOff = getString(R.string.prefs_auto_create_images_summary_low_memory)
        }

    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        libraryCategories.setOnPreferenceClickListener {
            LibraryCategoriesFragment.newInstance(MediaIdCategory.SONGS)
                    .show(activity!!.supportFragmentManager, LibraryCategoriesFragment.TAG)
            true
        }
        podcastCategories.setOnPreferenceClickListener {
            LibraryCategoriesFragment.newInstance(MediaIdCategory.PODCASTS)
                    .show(activity!!.supportFragmentManager, LibraryCategoriesFragment.TAG)
            true
        }
        blacklist.setOnPreferenceClickListener {
            act.fragmentTransaction {
                setReorderingAllowed(true)
                add(BlacklistFragment.newInstance(), BlacklistFragment.TAG)
            }
            true
        }

        deleteCache.setOnPreferenceClickListener {
            showDeleteAllCacheDialog()
            true
        }
        lastFmCredentials.setOnPreferenceClickListener {
            act.fragmentTransaction {
                setReorderingAllowed(true)
                add(LastFmCredentialsFragment.newInstance(), LastFmCredentialsFragment.TAG)
            }
            true
        }
        accentColorChooser.setOnPreferenceClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(act.applicationContext)
            val key = getString(if (AppTheme.isWhiteTheme()) R.string.prefs_accent_light_key else R.string.prefs_accent_dark_key)
            val defaultColor = ContextCompat.getColor(act, (if (AppTheme.isWhiteTheme()) R.color.accent else R.color.accent_secondary))

            MaterialDialog(act)
                    .colorChooser(
                            colors = ColorPalette.ACCENT_COLORS,
                            subColors = ColorPalette.ACCENT_COLORS_SUB,
                            initialSelection = prefs.getInt(key, defaultColor),
                            selection = act as PreferencesActivity
                    ).show()
            true
        }
        resetTutorial.setOnPreferenceClickListener {
            showResetTutorialDialog()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        libraryCategories.onPreferenceClickListener = null
        podcastCategories.onPreferenceClickListener = null
        blacklist.onPreferenceClickListener = null
        deleteCache.onPreferenceClickListener = null
        lastFmCredentials.onPreferenceClickListener = null
        accentColorChooser.onPreferenceClickListener = null
        resetTutorial.onPreferenceClickListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key){
            getString(R.string.prefs_quick_action_key) -> {
                AppConstants.updateQuickAction(act)
                requestMainActivityToRecreate()
            }
            getString(R.string.prefs_icon_shape_key) -> {
                AppConstants.updateIconShape(act)
                requestMainActivityToRecreate()
            }
            getString(R.string.prefs_dark_mode_key) -> {
                AppTheme.updateDarkMode(act)
                requestMainActivityToRecreate()
                act.finish()
                act.startActivity(Intent(act, act::class.java),
                        bundleOf(PreferencesActivity.EXTRA_NEED_TO_RECREATE to needsToRecreate)
                )
            }
            getString(R.string.prefs_appearance_key) -> {
                AppTheme.updateTheme(act)
                requestMainActivityToRecreate()
            }
            getString(R.string.prefs_notch_support_key),
            getString(R.string.prefs_folder_tree_view_key),
            getString(R.string.prefs_blacklist_key),
            getString(R.string.prefs_show_podcasts_key) -> requestMainActivityToRecreate()
        }
    }

    fun requestMainActivityToRecreate(){
        needsToRecreate = true
        act.setResult(Activity.RESULT_OK)
    }

    private fun showDeleteAllCacheDialog(){
        ThemedDialog.builder(ctx)
                .setTitle(R.string.prefs_delete_cached_images_title)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                    GlideApp.get(ctx.applicationContext).clearMemory()
                    @Suppress("UNUSED_VARIABLE")
                    val disp = Completable.fromCallable {
                        GlideApp.get(ctx.applicationContext).clearDiskCache()
                        ImagesFolderUtils.getImageFolderFor(ctx, ImagesFolderUtils.getFolderName(ImagesFolderUtils.FOLDER)).listFiles().forEach { it.delete() }
                        ImagesFolderUtils.getImageFolderFor(ctx, ImagesFolderUtils.getFolderName(ImagesFolderUtils.PLAYLIST)).listFiles().forEach { it.delete() }
                        ImagesFolderUtils.getImageFolderFor(ctx, ImagesFolderUtils.getFolderName(ImagesFolderUtils.GENRE)).listFiles().forEach { it.delete() }
                    }.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                requestMainActivityToRecreate()
                                ctx.applicationContext.toast(R.string.prefs_delete_cached_images_success)
                            }, Throwable::printStackTrace)
                }
                .setNegativeButton(R.string.popup_negative_no, null)
                .show()
    }

    private fun showResetTutorialDialog(){
        ThemedDialog.builder(ctx)
                .setTitle(R.string.prefs_reset_tutorial_title)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                    tutorialPrefsUseCase.reset()
                }
                .setNegativeButton(R.string.popup_negative_no, null)
                .show()
    }

}