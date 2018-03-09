package dev.olog.msc.presentation.navigator

import android.content.Intent
import android.net.Uri
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import dev.olog.msc.R
import dev.olog.msc.presentation.licenses.LicensesFragment
import dev.olog.msc.presentation.special.thanks.SpecialThanksFragment
import dev.olog.msc.presentation.utils.openPlayStore
import dev.olog.msc.presentation.utils.openReportBugs
import dev.olog.msc.utils.k.extension.fragmentTransaction
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 600 // ms

class NavigatorAboutImpl @Inject internal constructor(
        private val activity: AppCompatActivity

) : NavigatorAbout {

    private var lastRequest: Long = -1

    override fun toLicensesFragment() {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, LicensesFragment(), LicensesFragment.TAG)
                addToBackStack(LicensesFragment.TAG)
            }
        }
    }

    override fun toSpecialThanksFragment() {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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

    override fun reportBugs() {
        if (allowed()){
           openReportBugs(activity)
        }
    }

    override fun toPrivacyPolicy() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/about")
            activity.startActivity(intent)
        }
    }

    override fun toWebsite() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next")
            activity.startActivity(intent)
        }
    }

    override fun toFacebook() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.facebook.com/eugeniu.olog")
            activity.startActivity(intent)
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

}