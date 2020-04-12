package dev.olog.feature.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialSharedAxis
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.about.about.AboutFragment
import dev.olog.feature.about.license.LicensesFragment
import dev.olog.feature.about.special.thanks.SpecialThanksFragment
import dev.olog.feature.about.translation.TranslationsFragment
import dev.olog.shared.android.extensions.isIntentSafe
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.feature.presentation.base.utils.PlayStoreUtils
import dev.olog.shared.android.extensions.colorSurface
import saschpe.android.customtabs.CustomTabsHelper
import javax.inject.Inject

@FeatureScope
internal class NavigatorAbout @Inject constructor(

) {

    fun toLicensesFragment(activity: FragmentActivity) {

        val current = activity.supportFragmentManager.findFragmentByTag(AboutFragment.TAG)!!
        current.exitTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        current.reenterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        val fragment = LicensesFragment()
        fragment.enterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        fragment.returnTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        activity.supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment, LicensesFragment.TAG)
            addToBackStack(LicensesFragment.TAG)
        }
    }

    fun toChangelog(activity: FragmentActivity) {

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

    fun toGithub(activity: FragmentActivity) {

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

    fun toSpecialThanksFragment(activity: FragmentActivity) {
        val current = activity.supportFragmentManager.findFragmentByTag(AboutFragment.TAG)!!
        current.exitTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        current.reenterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        val fragment = SpecialThanksFragment()
        fragment.enterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        fragment.returnTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        activity.supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment, SpecialThanksFragment.TAG)
            addToBackStack(SpecialThanksFragment.TAG)
        }
    }

    fun toMarket(activity: FragmentActivity) {
        PlayStoreUtils.open(activity)
    }

    fun toPrivacyPolicy(activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }

    fun joinCommunity(activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://www.reddit.com/r/canaree/")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }

    fun joinBeta(activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }

    fun toTranslations(activity: FragmentActivity) {
        val current = activity.supportFragmentManager.findFragmentByTag(AboutFragment.TAG)!!
        current.exitTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        current.reenterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        val fragment = TranslationsFragment()
        fragment.enterTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, true)
        fragment.returnTransition = MaterialSharedAxis.create(activity, MaterialSharedAxis.X, false)

        activity.supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment, TranslationsFragment.TAG)
            addToBackStack(TranslationsFragment.TAG)
        }
    }

    fun requestTranslation(activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://canaree.oneskyapp.com/collaboration/project/162621")
        if (activity.packageManager.isIntentSafe(intent)) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.common_browser_not_found)
        }
    }
}