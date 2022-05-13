package dev.olog.feature.splash

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import javax.inject.Inject

class FeatureSplashNavigatorImpl @Inject constructor(

) : FeatureSplashNavigator {

    override fun toSplash(activity: FragmentActivity) {
        activity.supportFragmentManager.commit {
            add(android.R.id.content, SplashFragment(), SplashFragment.TAG)
        }
    }

}