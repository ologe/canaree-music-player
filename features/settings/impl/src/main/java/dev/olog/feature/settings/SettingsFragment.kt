package dev.olog.feature.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
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
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.library.api.LibraryPage
import dev.olog.feature.library.api.LibraryPreferences
import dev.olog.feature.settings.blacklist.BlacklistFragment
import dev.olog.feature.settings.last.fm.LastFmCredentialsFragment
import dev.olog.feature.settings.library.LibraryCategoriesFragment
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.creator.ImagesFolderUtils
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.isDarkMode
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.toast
import dev.olog.ui.ColorPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Keep
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    ColorCallback,
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val TAG = FragmentTagFactory.create(SettingsFragment::class)
    }

    @Inject
    internal lateinit var tutorialPrefsUseCase: TutorialPreferenceGateway

    @Inject
    internal lateinit var libraryPrefs: LibraryPreferences

    private lateinit var libraryCategories: Preference
    private lateinit var podcastCategories: Preference
    private lateinit var blacklist: Preference
    private lateinit var iconShape: Preference
    private lateinit var deleteCache: Preference
    private lateinit var lastFmCredentials: Preference
    private lateinit var autoCreateImages: Preference
    private lateinit var accentColorChooser: Preference
    private lateinit var resetTutorial: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
        libraryCategories = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_library_categories_key))!!
        podcastCategories = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_podcast_library_categories_key))!!
        blacklist = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_blacklist_key))!!
        iconShape = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_icon_shape_key))!!
        deleteCache = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_delete_cached_images_key))!!
        lastFmCredentials = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_last_fm_credentials_key))!!
        autoCreateImages = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_auto_create_images_key))!!
        accentColorChooser = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_color_accent_key))!!
        resetTutorial = preferenceScreen.findPreference(getString(dev.olog.feature.settings.api.R.string.prefs_reset_tutorial_key))!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = view.findViewById<RecyclerView>(androidx.preference.R.id.recycler_view)
        list.layoutManager = OverScrollLinearLayoutManager(list)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

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
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
            val key = getString(dev.olog.feature.settings.api.R.string.prefs_color_accent_key)
            val defaultColor = ContextCompat.getColor(requireContext(), dev.olog.ui.R.color.defaultColorAccent)

            MaterialDialog(requireContext())
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
        if (context == null) {
            return
            // crash workaround, don't know if crashes because of a leak or what else
        }
        when (key) {
            getString(dev.olog.feature.settings.api.R.string.prefs_folder_tree_view_key) -> {
                requireActivity().recreate()
            }
            getString(dev.olog.feature.settings.api.R.string.prefs_show_podcasts_key) -> {
                libraryPrefs.setLibraryPage(LibraryPage.TRACKS)
                requireActivity().recreate()
            }
        }
    }

    private fun showDeleteAllCacheDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(localization.R.string.prefs_delete_cached_images_title)
            .setMessage(localization.R.string.are_you_sure)
            .setPositiveButton(localization.R.string.popup_positive_ok) { _, _ -> launchWhenResumed { clearGlideCache() } }
            .setNegativeButton(localization.R.string.popup_negative_no, null)
            .show()
    }

    private suspend fun clearGlideCache() {
        val context = requireContext().applicationContext
        GlideApp.get(context).clearMemory()

        withContext(Dispatchers.IO) {
            GlideApp.get(context).clearDiskCache()
            ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.FOLDER).listFiles()
                ?.forEach { it.delete() }
            ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.PLAYLIST).listFiles()
                ?.forEach { it.delete() }
            ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.GENRE).listFiles()
                ?.forEach { it.delete() }
        }
        toast(localization.R.string.prefs_delete_cached_images_success)
    }

    private fun showResetTutorialDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(localization.R.string.prefs_reset_tutorial_title)
            .setMessage(localization.R.string.are_you_sure)
            .setPositiveButton(localization.R.string.popup_positive_ok) { _, _ -> tutorialPrefsUseCase.reset() }
            .setNegativeButton(localization.R.string.popup_negative_no, null)
            .show()
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        val realColor = ColorPalette.getRealAccentSubColor(requireContext().isDarkMode(), color)
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val key = getString(dev.olog.feature.settings.api.R.string.prefs_color_accent_key)
        prefs.edit {
            putInt(key, realColor)
        }
        requireActivity().recreate()
    }
}