package dev.olog.presentation.navigation

import android.content.Intent
import android.net.Uri
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dev.olog.presentation.R
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.fragment_licenses.LicensesFragment
import dev.olog.presentation.fragment_privacy_policy.PrivacyPolicyFragment
import dev.olog.presentation.fragment_special_thanks.SpecialThanksFragment
import dev.olog.presentation.utils.extension.transaction
import javax.inject.Inject

@PerActivity
class NavigatorAboutImpl @Inject constructor(
        private val activity: AppCompatActivity

) : NavigatorAbout {

    companion object {
        private const val NEXT_REQUEST_THRESHOLD: Long = 600 // ms
    }

    private var lastRequest: Long = -1

    override fun toLicensesFragment() {
        if (allowed()) {
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, LicensesFragment(), LicensesFragment.TAG)
                addToBackStack(LicensesFragment.TAG)
            }
        }
    }

    override fun toSpecialThanksFragment() {
        if (allowed()) {
            activity.supportFragmentManager.transaction {
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

    override fun toPrivacyPolicy() {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, PrivacyPolicyFragment(), PrivacyPolicyFragment.TAG)
                addToBackStack(PrivacyPolicyFragment.TAG)
            }
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

}