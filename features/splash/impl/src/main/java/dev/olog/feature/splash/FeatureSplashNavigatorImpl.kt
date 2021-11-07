package dev.olog.feature.splash

import androidx.fragment.app.FragmentActivity
import dev.olog.shared.android.extensions.fragmentTransaction
import javax.inject.Inject

class FeatureSplashNavigatorImpl @Inject constructor(

) : FeatureSplashNavigator {

    override fun toFirstAccess(activity: FragmentActivity) {
        activity.fragmentTransaction {
            add(android.R.id.content, SplashFragment(), SplashFragment.TAG)
        }
    }
}