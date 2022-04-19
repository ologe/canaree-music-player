package dev.olog.presentation.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.presentation.R
import dev.olog.presentation.license.LicensesFragment
import dev.olog.presentation.thanks.SpecialThanksFragment
import dev.olog.presentation.translations.TranslationsFragment
import dev.olog.shared.android.extensions.colorSurface
import dev.olog.shared.android.extensions.isIntentSafe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.PlayStoreUtils
import saschpe.android.customtabs.CustomTabsHelper
import javax.inject.Inject

class NavigatorAboutImpl @Inject internal constructor(
    private val activity: FragmentActivity

) : NavigatorAbout {

    private val callback = object : CustomTabsHelper.CustomTabFallback {
        override fun openUri(context: Context?, uri: Uri?) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun toHavocPage() {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=dev.olog.havoc")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun toLicensesFragment() {
        superCerealTransition(
            activity, LicensesFragment(), LicensesFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toChangelog() {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, callback)
    }

    override fun toGithub() {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, callback)
    }

    override fun toSpecialThanksFragment() {
        superCerealTransition(
            activity, SpecialThanksFragment(), SpecialThanksFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toMarket() {
        if (allowed()) {
            PlayStoreUtils.open(activity)
        }
    }

    override fun toPrivacyPolicy() {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun joinCommunity() {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.reddit.com/r/canaree/")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun joinBeta() {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun toTranslations() {
        superCerealTransition(
            activity, TranslationsFragment(), TranslationsFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun requestTranslation() {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://canaree.oneskyapp.com/collaboration/project/162621")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }
}