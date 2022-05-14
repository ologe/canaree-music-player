package dev.olog.platform.navigation

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.platform.BottomNavigationFragmentTag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor(
    private val bottomNavigationFragmentTags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
) {

    fun navigateToFragment(
        activity: FragmentActivity,
        fragment: Fragment,
        tag: String? = null,
        transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE,
    ) {
        superCerealTransition(
            activity = activity,
            fragment = fragment,
            tag = tag,
            tags = bottomNavigationFragmentTags,
            transition = transition,
        )
    }

    // todo test fallback when chrome is not installed
    fun openUrl(
        activity: FragmentActivity,
        url: String,
    ) {
        if (!allowed()) {
            return
        }
        CustomTabsIntent.Builder()
            .setUrlBarHidingEnabled(false)
            .build()
            .launchUrl(activity, Uri.parse(url))
    }

}