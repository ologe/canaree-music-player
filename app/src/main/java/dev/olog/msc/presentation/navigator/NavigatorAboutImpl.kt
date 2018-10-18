package dev.olog.msc.presentation.navigator

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import dev.olog.msc.R
import dev.olog.msc.presentation.licenses.LicensesFragment
import dev.olog.msc.presentation.special.thanks.SpecialThanksFragment
import dev.olog.msc.presentation.utils.openPlayStore
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.msc.utils.k.extension.isIntentSafe
import dev.olog.msc.utils.k.extension.toast
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

class NavigatorAboutImpl @Inject internal constructor(
        private val activity: AppCompatActivity

) : NavigatorAbout {

    private var lastRequest: Long = -1

    override fun toLicensesFragment() {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, LicensesFragment(), LicensesFragment.TAG)
                addToBackStack(LicensesFragment.TAG)
            }
        }
    }

    override fun toSpecialThanksFragment() {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, SpecialThanksFragment(), SpecialThanksFragment.TAG)
                addToBackStack(SpecialThanksFragment.TAG)
            }
        }
    }

    override fun toMarket() {
        if (allowed()){
            openPlayStore(activity)
        }
    }

    override fun toPrivacyPolicy() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

//    override fun toDeveloperProfile() {
//        if (allowed()){
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("https://www.facebook.com/eugeniu.olog")
//            if (activity.packageManager.isIntentSafe(intent)) {
//                activity.startActivity(intent)
//            } else {
//                activity.toast(R.string.common_browser_not_found)
//            }
//        }
//    }

    override fun joinCommunity() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://plus.google.com/u/1/communities/112263979767803607353")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun joinBeta() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast(R.string.common_browser_not_found)
            }
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

}