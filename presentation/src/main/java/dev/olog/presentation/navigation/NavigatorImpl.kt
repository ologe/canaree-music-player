package dev.olog.presentation.navigation

import android.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.utils.transaction
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

@PerActivity
class NavigatorImpl @Inject constructor(
        private val activity: AppCompatActivity

): Navigator {

    companion object {
        private const val NEXT_REQUEST_THRESHOLD: Long = 600 // ms
    }

    private var lastRequest: Long = -1

    override fun toMainActivity() {
        activity.startActivity(
                activity.intentFor<MainActivity>()
                .clearTop()
                .newTask()
        )
        activity.finish()
    }

    override fun toDetailActivity(mediaId: String) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.viewPagerLayout,
                            DetailFragment.newInstance(mediaId),
                            DetailFragment.TAG)
                addToBackStack(DetailFragment.TAG)
            }
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }
}
