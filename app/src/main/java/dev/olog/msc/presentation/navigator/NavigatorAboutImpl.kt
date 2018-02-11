package dev.olog.msc.presentation.navigator

import android.content.Intent
import android.net.Uri
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dev.olog.msc.R
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.presentation.licenses.LicensesFragment
import dev.olog.msc.presentation.thanks.SpecialThanksFragment
import dev.olog.msc.utils.k.extension.fragmentTransaction
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 600 // ms

@PerActivity
class NavigatorAboutImpl @Inject constructor(
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
            val uri = Uri.parse("market://details?id=${activity.packageName}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            } else {
                Log.w("Navigator", "google play market not found")
            }
        }
    }

    override fun reportBugs() {
        if (allowed()){
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dev.eugeniu.olog@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Next bug report")
            activity.startActivity(intent)
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