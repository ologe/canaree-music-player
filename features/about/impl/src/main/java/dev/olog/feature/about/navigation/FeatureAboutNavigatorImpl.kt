package dev.olog.feature.about.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.feature.about.AboutFragment
import dev.olog.feature.about.api.FeatureAboutNavigator
import dev.olog.feature.about.license.LicensesFragment
import dev.olog.feature.about.thanks.SpecialThanksFragment
import dev.olog.platform.navigation.NavigationManager
import javax.inject.Inject

class FeatureAboutNavigatorImpl @Inject constructor(
    private val manager: NavigationManager,
) : FeatureAboutNavigator {

    override fun toAbout(activity: FragmentActivity) {
        manager.navigateToFragment(
            activity = activity,
            fragment = AboutFragment(),
        )
    }

    override fun toLicensesFragment(activity: FragmentActivity) {
        manager.navigateToFragment(
            activity = activity,
            fragment = LicensesFragment(),
            transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE,
        )
    }

    override fun toChangelog(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md"
        )
    }

    override fun toGithub(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://github.com/ologe/canaree-music-player"
        )
    }

    override fun toSpecialThanksFragment(activity: FragmentActivity) {
        manager.navigateToFragment(
            activity = activity,
            fragment = SpecialThanksFragment(),
            transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toHavocPage(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://play.google.com/store/apps/details?id=dev.olog.havoc"
        )
    }

    override fun toMarket(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://play.google.com/store/apps/details?id=dev.olog.msc"
        )
    }

    override fun toPrivacyPolicy(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://deveugeniuolog.wixsite.com/next/privacy-policy"
        )
    }

    override fun joinCommunity(activity: FragmentActivity)  {
        manager.openUrl(
            activity = activity,
            url = "https://www.reddit.com/r/canaree/"
        )
    }

    override fun joinBeta(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://play.google.com/apps/testing/dev.olog.msc"
        )
    }

    override fun toTranslations(activity: FragmentActivity) {
        manager.openUrl(
            activity = activity,
            url = "https://canaree.oneskyapp.com/collaboration/project/162621"
        )
    }

}