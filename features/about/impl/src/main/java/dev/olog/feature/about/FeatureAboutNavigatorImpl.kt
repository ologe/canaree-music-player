package dev.olog.feature.about

import androidx.fragment.app.FragmentActivity
import dev.olog.platform.BottomNavigationFragmentTag
import dev.olog.platform.superCerealTransition
import javax.inject.Inject

class FeatureAboutNavigatorImpl @Inject constructor(
    private val tags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
) : FeatureAboutNavigator {

    override fun toAbout(activity: FragmentActivity) {
//        superCerealTransition( todo
//            activity = activity,
//            fragment = AboutFragment(),
//            tag = AboutFragment.TAG,
//            tags = tags,
//        )
    }
}