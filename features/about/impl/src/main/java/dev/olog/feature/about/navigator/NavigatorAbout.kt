package dev.olog.feature.about.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dagger.hilt.android.scopes.ActivityScoped
import dev.olog.feature.about.license.LicensesFragment
import dev.olog.feature.about.thanks.SpecialThanksFragment
import dev.olog.feature.about.translations.TranslationsFragment
import dev.olog.feature.base.allowed
import dev.olog.feature.base.superCerealTransition
import dev.olog.shared.android.extensions.colorSurface
import dev.olog.shared.android.extensions.isIntentSafe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.PlayStoreUtils
import saschpe.android.customtabs.CustomTabsHelper
import java.lang.ref.WeakReference
import javax.inject.Inject

@ActivityScoped // TODO change to singleton
class NavigatorAbout @Inject constructor(
    activity: FragmentActivity

) {

    private val activityRef = WeakReference(activity)

    private val callback = object : CustomTabsHelper.CustomTabFallback {
        override fun openUri(context: Context?, uri: Uri?) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }

    fun toLicensesFragment() {
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, LicensesFragment(), LicensesFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    fun toChangelog() {
        val activity = activityRef.get() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, callback)
    }

    fun toGithub() {
        val activity = activityRef.get() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, callback)
    }

    fun toSpecialThanksFragment() {
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, SpecialThanksFragment(), SpecialThanksFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    fun toMarket() {
        val activity = activityRef.get() ?: return

        if (allowed()) {
            PlayStoreUtils.open(activity)
        }
    }

    fun toPrivacyPolicy() {
        val activity = activityRef.get() ?: return

        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }

    fun joinCommunity() {
        val activity = activityRef.get() ?: return

        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.reddit.com/r/canaree/")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }

    fun joinBeta() {
        val activity = activityRef.get() ?: return

        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }

    fun toTranslations() {
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, TranslationsFragment(), TranslationsFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    fun requestTranslation() {
        val activity = activityRef.get() ?: return

        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://canaree.oneskyapp.com/collaboration/project/162621")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }
}