package dev.olog.feature.about.api

import androidx.fragment.app.FragmentActivity

interface FeatureAboutNavigator {

    fun toAbout(activity: FragmentActivity)

    fun toHavocPage(activity: FragmentActivity)
    fun toLicensesFragment(activity: FragmentActivity)

    fun toSpecialThanksFragment(activity: FragmentActivity)

    fun toMarket(activity: FragmentActivity)

    fun toPrivacyPolicy(activity: FragmentActivity)

    fun joinCommunity(activity: FragmentActivity)

    fun joinBeta(activity: FragmentActivity)

    fun toChangelog(activity: FragmentActivity)

    fun toGithub(activity: FragmentActivity)

    fun toTranslations(activity: FragmentActivity)

    fun requestTranslation(activity: FragmentActivity)

}