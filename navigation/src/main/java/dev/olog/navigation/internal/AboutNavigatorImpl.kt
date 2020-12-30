package dev.olog.navigation.internal

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dev.olog.navigation.AboutNavigator
import dev.olog.navigation.destination.FragmentScreen
import saschpe.android.customtabs.CustomTabsHelper
import javax.inject.Inject
import javax.inject.Provider

internal class AboutNavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : BaseNavigator(), AboutNavigator {

    override fun toLicensesFragment() {
        navigate(FragmentScreen.LICENSE)
    }

    override fun toSpecialThanksFragment() {
        navigate(FragmentScreen.SPECIAL_THANKS)
    }

    override fun toPrivacyPolicy() {
        openCustomTab("https://deveugeniuolog.wixsite.com/next/privacy-policy")
    }

    override fun joinCommunity() {
        openCustomTab("https://www.reddit.com/r/canaree/")
    }

    override fun joinBeta() {
        openCustomTab("https://play.google.com/apps/testing/dev.olog.msc")
    }

    override fun toChangelog() {
        openCustomTab("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
    }

    override fun toGithub() {
        openCustomTab("https://github.com/ologe/canaree-music-player")
    }

    override fun toLocalization() {
        navigate(FragmentScreen.LOCALIZATION)
    }

    override fun requestTranslation() {
        openCustomTab("https://canaree.oneskyapp.com/collaboration/project/162621")
    }

    private fun navigate(screen: FragmentScreen) {
        val activity = activityProvider() ?: return
        val fragment = fragments[screen]?.get()
        val tag = screen.tag

        replaceFragment(
            activity = activity,
            fragment = fragment,
            tag = tag,
        ) {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        }
    }

    private fun openCustomTab(url: String) {
        val activity = activityProvider() ?: return

        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
//            .setToolbarColor(activity.colorSurface()) // TODO
            .build()
        CustomTabsHelper.addKeepAliveExtra(activity, customTabIntent.intent)

        val uri = Uri.parse(url)
        CustomTabsHelper.openCustomTab(
            activity,
            customTabIntent,
            uri,
            object : CustomTabsHelper.CustomTabFallback {
                override fun openUri(context: Context?, uri: Uri?) {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    if (activity.packageManager.queryIntentActivities(intent, 0).isNotEmpty()) {
                        activity.startActivity(intent)
                    } else {
//                        activity.toast(R.string.common_browser_not_found) // TODO snackbar?
                    }
                }
            }
        )
    }

}