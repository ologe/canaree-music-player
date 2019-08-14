package dev.olog.presentation.navigator

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentTransaction
import dev.olog.presentation.R
import dev.olog.presentation.license.LicensesFragment
import dev.olog.presentation.thanks.SpecialThanksFragment
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.utils.PlayStoreUtils
import saschpe.android.customtabs.CustomTabsHelper
import java.lang.ref.WeakReference
import javax.inject.Inject

class NavigatorAboutImpl @Inject internal constructor(
    activity: AppCompatActivity

) : NavigatorAbout {

    private val activityRef = WeakReference(activity)

    override fun toLicensesFragment() {
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, LicensesFragment(), LicensesFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toChangelog() {
        val activity = activityRef.get() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri =
            Uri.parse("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun toGithub() {
        val activity = activityRef.get() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun toSpecialThanksFragment() {
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, SpecialThanksFragment(), SpecialThanksFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toMarket() {
        val activity = activityRef.get() ?: return

        if (allowed()) {
            PlayStoreUtils.open(activity)
        }
    }

    override fun toPrivacyPolicy() {
        val activity = activityRef.get() ?: return

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
        val activity = activityRef.get() ?: return

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
        val activity = activityRef.get() ?: return

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

}