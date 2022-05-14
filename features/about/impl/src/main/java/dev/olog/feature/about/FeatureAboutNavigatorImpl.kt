package dev.olog.feature.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.feature.about.api.FeatureAboutNavigator
import dev.olog.feature.about.license.LicensesFragment
import dev.olog.feature.about.thanks.SpecialThanksFragment
import dev.olog.feature.about.translation.TranslationsFragment
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.PlayStoreUtils
import dev.olog.platform.allowed
import dev.olog.platform.superCerealTransition
import dev.olog.shared.extension.isIntentSafe
import dev.olog.shared.extension.toast
import dev.olog.ui.colorSurface
import saschpe.android.customtabs.CustomTabsHelper
import javax.inject.Inject

class FeatureAboutNavigatorImpl @Inject constructor(
    private val tags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
) : FeatureAboutNavigator {

    override fun toAbout(activity: FragmentActivity) {
        superCerealTransition(
            activity = activity,
            fragment = AboutFragment(),
            tag = AboutFragment.TAG,
            tags = tags,
        )
    }

    override fun toHavocPage(activity: FragmentActivity) {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=dev.olog.havoc")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }

    override fun toLicensesFragment(activity: FragmentActivity) {
        superCerealTransition(
            activity = activity, fragment = LicensesFragment(),
            tag = LicensesFragment.TAG,
            tags = tags,
            transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE,
        )
    }

    override fun toChangelog(activity: FragmentActivity) {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, Callback(activity))
    }

    override fun toGithub(activity: FragmentActivity) {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(activity.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player")
        CustomTabsHelper.openCustomTab(activity, customTabIntent, uri, Callback(activity))
    }

    override fun toSpecialThanksFragment(activity: FragmentActivity) {
        superCerealTransition(
            activity = activity,
            fragment = SpecialThanksFragment(),
            tag = SpecialThanksFragment.TAG,
            tags = tags,
            transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toMarket(activity: FragmentActivity) {
        if (allowed()) {
            PlayStoreUtils.open(activity)
        }
    }

    override fun toPrivacyPolicy(activity: FragmentActivity) {
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

    override fun joinCommunity(activity: FragmentActivity) {
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

    override fun joinBeta(activity: FragmentActivity) {
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

    override fun toTranslations(activity: FragmentActivity) {
        superCerealTransition(
            activity = activity,
            fragment = TranslationsFragment(),
            tag = TranslationsFragment.TAG,
            tags = tags,
            transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun requestTranslation(activity: FragmentActivity) {
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

    private class Callback(private val activity: FragmentActivity) : CustomTabsHelper.CustomTabFallback {
        override fun openUri(context: Context?, uri: Uri?) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(localization.R.string.common_browser_not_found)
            }
        }
    }

}