package dev.olog.feature.about

import androidx.fragment.app.FragmentActivity
import dev.olog.feature.base.superCerealTransition
import javax.inject.Inject

class FeatureAboutNavigatorImpl @Inject constructor(

) : FeatureAboutNavigator {

    override fun toAboutActivity(activity: FragmentActivity) {
        superCerealTransition(
            activity = activity,
            fragment = AboutFragment(),
            tag = AboutFragment.TAG
        )
    }
}