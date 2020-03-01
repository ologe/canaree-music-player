package dev.olog.presentation.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import com.google.android.material.transition.MaterialSharedAxis
import dev.olog.presentation.R
import dev.olog.presentation.about.AboutFragment
import dev.olog.presentation.license.LicensesFragment
import dev.olog.presentation.thanks.SpecialThanksFragment
import dev.olog.presentation.translations.TranslationsFragment
import dev.olog.shared.android.extensions.colorSurface
import dev.olog.shared.android.extensions.fragmentTransaction
import dev.olog.shared.android.extensions.isIntentSafe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.PlayStoreUtils
import dev.olog.shared.mandatory
import saschpe.android.customtabs.CustomTabsHelper
import java.lang.ref.WeakReference
import javax.inject.Inject

class NavigatorAboutImpl @Inject internal constructor(
    activity: FragmentActivity

) : NavigatorAbout {

    private val activityRef = WeakReference(activity)

    override fun toLicensesFragment() {
        mandatory(allowed()) ?: return
        val activity = activityRef.get() ?: return

        val current = activity.supportFragmentManager.findFragmentByTag(AboutFragment.TAG)
        current!!.exitTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        current!!.reenterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        val fragment = LicensesFragment()
        fragment.enterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        fragment.returnTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        activity.fragmentTransaction {
            replace(R.id.fragmentContainer, fragment, LicensesFragment.TAG)
            addToBackStack(LicensesFragment.TAG)
        }
    }

    override fun toChangelog() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri =
            Uri.parse("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, object: CustomTabsHelper.CustomTabFallback {
            override fun openUri(context: Context?, uri: Uri?) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (activity.packageManager.isIntentSafe(intent)) {
                    activity.startActivity(intent)
                } else {
                    activity.toast(R.string.common_browser_not_found)
                }
            }
        })
    }

    override fun toGithub() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, object: CustomTabsHelper.CustomTabFallback {
            override fun openUri(context: Context?, uri: Uri?) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (activity.packageManager.isIntentSafe(intent)) {
                    activity.startActivity(intent)
                } else {
                    activity.toast(R.string.common_browser_not_found)
                }
            }
        })
    }

    override fun toSpecialThanksFragment() {
        mandatory(allowed()) ?: return
        val activity = activityRef.get() ?: return

        val current = activity.supportFragmentManager.findFragmentByTag(AboutFragment.TAG)
        current!!.exitTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        current!!.reenterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        val fragment = SpecialThanksFragment()
        fragment.enterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        fragment.returnTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        activity.fragmentTransaction {
            replace(R.id.fragmentContainer, fragment, SpecialThanksFragment.TAG)
            addToBackStack(SpecialThanksFragment.TAG)
        }
    }

    override fun toMarket() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        PlayStoreUtils.open(activity)
    }

    override fun toPrivacyPolicy() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }

    override fun joinCommunity() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://www.reddit.com/r/canaree/")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }

    override fun joinBeta() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }

    override fun toTranslations() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val current = activity.supportFragmentManager.findFragmentByTag(AboutFragment.TAG)
        current!!.exitTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        current!!.reenterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        val fragment = TranslationsFragment()
        fragment.enterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        fragment.returnTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        activity.fragmentTransaction {
            replace(R.id.fragmentContainer, fragment, TranslationsFragment.TAG)
            addToBackStack(TranslationsFragment.TAG)
        }
    }

    override fun requestTranslation() {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://canaree.oneskyapp.com/collaboration/project/162621")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }
}