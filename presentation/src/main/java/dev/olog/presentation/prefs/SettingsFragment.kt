package dev.olog.presentation.prefs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import dev.olog.core.MediaIdCategory
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.creator.ImagesFolderUtils
import dev.olog.presentation.R
import dev.olog.presentation.prefs.blacklist.BlacklistFragment
import dev.olog.presentation.prefs.categories.LibraryCategoriesFragment
import dev.olog.presentation.prefs.lastfm.LastFmCredentialsFragment
import dev.olog.presentation.pro.HasBilling
import dev.olog.presentation.utils.forEach
import dev.olog.shared.extensions.*
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), ColorCallback {

    companion object {
        val TAG = SettingsFragment::class.java.name
    }

    @Inject lateinit var tutorialPrefsUseCase: TutorialPreferenceGateway

    private lateinit var libraryCategories : Preference
    private lateinit var podcastCategories : Preference
    private lateinit var blacklist : Preference
    private lateinit var iconShape : Preference
    private lateinit var deleteCache : Preference
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

        val billing = (act as HasBilling).billing
        billing.observeBillingsState()
            .map { it.isPremiumEnabled() }
                .take(2) // take current and after check values
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(viewLifecycleOwner) { isPremium ->
                    preferenceScreen.forEach { it.isEnabled = isPremium }

                    if (!isPremium) {
                        val v = act.window.decorView.findViewById<View>(android.R.id.content)
                        Snackbar.make(v, R.string.prefs_not_premium, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.prefs_not_premium_action) { billing.purchasePremium() }
                                .show()
                    }
                }

    }

    override fun onResume() {
        super.onResume()
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
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
            val key = getString(R.string.prefs_color_accent_key)
            val defaultColor = ContextCompat.getColor(act, R.color.defaultColorAccent)

            MaterialDialog(act)
                    .colorChooser(
                            colors = ColorPalette.ACCENT_COLORS,
                            subColors = ColorPalette.ACCENT_COLORS_SUB,
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
        libraryCategories.onPreferenceClickListener = null
        podcastCategories.onPreferenceClickListener = null
        blacklist.onPreferenceClickListener = null
        deleteCache.onPreferenceClickListener = null
        lastFmCredentials.onPreferenceClickListener = null
        accentColorChooser.onPreferenceClickListener = null
        resetTutorial.onPreferenceClickListener = null
    }

    private fun showDeleteAllCacheDialog(){
        AlertDialog.Builder(ctx)
                .setTitle(R.string.prefs_delete_cached_images_title)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                    GlideApp.get(ctx.applicationContext).clearMemory()
                    @Suppress("UNUSED_VARIABLE")
                    val disp = Completable.fromCallable {
                        GlideApp.get(ctx.applicationContext).clearDiskCache()
                        ImagesFolderUtils.getImageFolderFor(ctx, ImagesFolderUtils.FOLDER).listFiles()?.forEach { it.delete() }
                        ImagesFolderUtils.getImageFolderFor(ctx, ImagesFolderUtils.PLAYLIST).listFiles()?.forEach { it.delete() }
                        ImagesFolderUtils.getImageFolderFor(ctx, ImagesFolderUtils.GENRE).listFiles()?.forEach { it.delete() }
                    }.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                ctx.applicationContext.toast(R.string.prefs_delete_cached_images_success)
                            }, Throwable::printStackTrace)
                }
                .setNegativeButton(R.string.popup_negative_no, null)
                .show()
    }

    private fun showResetTutorialDialog(){
        AlertDialog.Builder(ctx)
                .setTitle(R.string.prefs_reset_tutorial_title)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.popup_positive_ok) { _, _ -> tutorialPrefsUseCase.reset() }
                .setNegativeButton(R.string.popup_negative_no, null)
                .show()
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(act)
        val key = getString(R.string.prefs_color_accent_key)
        prefs.edit {
            putInt(key, color)
        }
        act.recreate()
    }
}