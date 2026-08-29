package dev.olog.presentation.navigator

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.presentation.R
import dev.olog.presentation.license.LicensesFragment
import dev.olog.presentation.thanks.SpecialThanksFragment
import dev.olog.presentation.translations.TranslationsFragment
import dev.olog.shared.android.extensions.isIntentSafe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.PlayStoreUtils
import java.lang.ref.WeakReference
import javax.inject.Inject

class NavigatorAboutImpl @Inject internal constructor(
    activity: FragmentActivity

) : NavigatorAbout {

    private val activityRef = WeakReference(activity)

    override fun toHavocPage() {
        val activity = activityRef.get() ?: return

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
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, LicensesFragment(), LicensesFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun toChangelog() {
        val activity = activityRef.get() ?: return

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player/blob/master/CHANGELOG.md")
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun toGithub() {
        val activity = activityRef.get() ?: return

        val uri = Uri.parse("https://github.com/ologe/canaree-music-player")
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
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

    override fun toTranslations() {
        val activity = activityRef.get() ?: return

        superCerealTransition(
            activity, TranslationsFragment(), TranslationsFragment.TAG,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
        )
    }

    override fun requestTranslation() {
        val activity = activityRef.get() ?: return

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