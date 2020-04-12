package dev.olog.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.AndroidSupportInjection
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.feature.settings.blacklist.BlacklistFragment
import dev.olog.feature.settings.categories.LibraryCategoriesFragment
import dev.olog.feature.settings.last.fm.LastFmCredentialsFragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.dark.mode.isDarkMode
import dev.olog.feature.presentation.base.extensions.launchWhenResumed
import dev.olog.shared.android.extensions.themeManager
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import dev.olog.lib.image.loader.ImageLoader
import dev.olog.navigation.screens.LibraryPage
import javax.inject.Inject

@Keep
internal class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    ColorCallback{

    companion object {
        @JvmStatic
        val TAG = SettingsFragment::class.java.name
    }

    @Inject
    internal lateinit var tutorialPrefsUseCase: TutorialPreferenceGateway

    @Inject
    internal lateinit var preferences: CommonPreferences

    private lateinit var libraryCategories: Preference
    private lateinit var podcastCategories: Preference
    private lateinit var blacklist: Preference
    private lateinit var iconShape: Preference
    private lateinit var deleteCache: Preference
    private lateinit var lastFmCredentials: Preference
    private lateinit var autoCreateImages: Preference
    private lateinit var accentColorChooser: Preference
    private lateinit var resetTutorial: Preference

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(R.string.prefs_library_categories_key))!!
        podcastCategories = preferenceScreen.findPreference(getString(R.string.prefs_podcast_library_categories_key))!!
        blacklist = preferenceScreen.findPreference(getString(R.string.prefs_blacklist_key))!!
        iconShape = preferenceScreen.findPreference(getString(R.string.prefs_icon_shape_key))!!
        deleteCache = preferenceScreen.findPreference(getString(R.string.prefs_delete_cached_images_key))!!
        lastFmCredentials = preferenceScreen.findPreference(getString(R.string.prefs_last_fm_credentials_key))!!
        autoCreateImages = preferenceScreen.findPreference(getString(R.string.prefs_auto_create_images_key))!!
        accentColorChooser = preferenceScreen.findPreference(getString(R.string.prefs_color_accent_key))!!
        resetTutorial = preferenceScreen.findPreference(getString(R.string.prefs_reset_tutorial_key))!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = view.findViewById<RecyclerView>(R.id.recycler_view)
        list.layoutManager = OverScrollLinearLayoutManager(list)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        libraryCategories.setOnPreferenceClickListener {
            LibraryCategoriesFragment.newInstance(MediaIdCategory.SONGS)
                .show(requireActivity().supportFragmentManager, LibraryCategoriesFragment.TAG)
            true
        }
        podcastCategories.setOnPreferenceClickListener {
            LibraryCategoriesFragment.newInstance(MediaIdCategory.PODCASTS)
                .show(requireActivity().supportFragmentManager, LibraryCategoriesFragment.TAG)
            true
        }
        blacklist.setOnPreferenceClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(BlacklistFragment.newInstance(), BlacklistFragment.TAG)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
            true
        }

        deleteCache.setOnPreferenceClickListener {
            showDeleteAllCacheDialog()
            true
        }
        lastFmCredentials.setOnPreferenceClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(LastFmCredentialsFragment.newInstance(), LastFmCredentialsFragment.TAG)
            }
            true
        }
        accentColorChooser.setOnPreferenceClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
            val key = getString(R.string.prefs_color_accent_key)
            val defaultColor = ContextCompat.getColor(requireActivity(), R.color.defaultColorAccent)

            MaterialDialog(requireActivity())
                .colorChooser(
                    colors = ColorPalette.getAccentColors(requireContext().isDarkMode()),
                    subColors = ColorPalette.getAccentColorsSub(requireContext().isDarkMode()),
                    initialSelection = prefs.getInt(key, defaultColor),
                    selection = this
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
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        libraryCategories.onPreferenceClickListener = null
        podcastCategories.onPreferenceClickListener = null
        blacklist.onPreferenceClickListener = null
        deleteCache.onPreferenceClickListener = null
        lastFmCredentials.onPreferenceClickListener = null
        accentColorChooser.onPreferenceClickListener = null
        resetTutorial.onPreferenceClickListener = null
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            getString(R.string.prefs_dark_mode_key) -> {
                val darkMode = themeManager.darkMode
                AppCompatDelegate.setDefaultNightMode(darkMode)
            }
            getString(R.string.prefs_icon_shape_key),
            getString(R.string.prefs_immersive_key),
            getString(R.string.prefs_appearance_key),
            getString(R.string.prefs_mini_player_appearance_key),
            getString(R.string.prefs_quick_action_key),
            getString(R.string.prefs_folder_tree_view_key) -> {
                requireActivity().recreate()
            }
            getString(R.string.prefs_show_podcasts_key) -> {
                preferences.setLibraryPage(LibraryPage.TRACKS)
                requireActivity().recreate()
            }
        }
    }

    private fun showDeleteAllCacheDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.prefs_delete_cached_images_title)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                launchWhenResumed { clearGlideCache() }
            }
            .setNegativeButton(R.string.popup_negative_no, null)
            .show()
    }

    @SuppressLint("ConcreteDispatcherIssue")
    private suspend fun clearGlideCache() {
        ImageLoader.clearCache(requireContext())
        requireContext().applicationContext.toast(R.string.prefs_delete_cached_images_success)
    }

    private fun showResetTutorialDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.prefs_reset_tutorial_title)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.popup_positive_ok) { _, _ -> tutorialPrefsUseCase.reset() }
            .setNegativeButton(R.string.popup_negative_no, null)
            .show()
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        val realColor = ColorPalette.getRealAccentSubColor(requireContext().isDarkMode(), color)
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val key = getString(R.string.prefs_color_accent_key)
        prefs.edit {
            putInt(key, realColor)
        }
        requireActivity().recreate()
    }
}