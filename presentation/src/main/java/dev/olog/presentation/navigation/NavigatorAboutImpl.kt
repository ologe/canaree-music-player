package dev.olog.presentation.navigation

import android.support.v7.app.AppCompatActivity
import dev.olog.presentation.R
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.fragment_licenses.LicensesFragment
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
                setCustomAnimations(
                        R.anim.right_slide_in,
                        R.anim.right_stay,
                        R.anim.left_stay,
                        R.anim.left_slide_out
                )
                add(android.R.id.content, LicensesFragment(), LicensesFragment.TAG)
                addToBackStack(LicensesFragment.TAG)
            }
        }
    }

    override fun toSpecialThanksFragment() {
        if (allowed()) {
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setCustomAnimations(
                        R.anim.right_slide_in,
                        R.anim.right_stay,
                        R.anim.left_stay,
                        R.anim.left_slide_out
                )
                add(android.R.id.content, SpecialThanksFragment(), SpecialThanksFragment.TAG)
                addToBackStack(SpecialThanksFragment.TAG)
            }
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

}