package dev.olog.presentation.navigator

import android.content.ActivityNotFoundException
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
import dev.olog.platform.extension.colorSurface
import dev.olog.platform.extension.toast
import dev.olog.platform.PlayStoreUtils
import saschpe.android.customtabs.CustomTabsHelper
import javax.inject.Inject

class NavigatorAboutImpl @Inject internal constructor(
    private val activity: FragmentActivity
) : NavigatorAbout {

    private val callback = object : CustomTabsHelper.CustomTabFallback {
        override fun openUri(context: Context?, uri: Uri?) {
            uri?.let { openUri(it) }
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

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player/blob/main/CHANGELOG.md")
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
            openUri("https://deveugeniuolog.wixsite.com/next/privacy-policy")
        }
    }

    override fun joinCommunity() {
        if (allowed()) {
            openUri("https://www.reddit.com/r/canaree/")
        }
    }

    override fun joinBeta() {
        if (allowed()) {
            openUri("https://play.google.com/apps/testing/dev.olog.msc")
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
            openUri("https://canaree.oneskyapp.com/collaboration/project/162621")
        }
    }

    private fun openUri(uri: String) {
        openUri(Uri.parse(uri))
    }

    private fun openUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            activity.toast(R.string.common_browser_not_found)
        }
    }

}