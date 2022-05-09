package dev.olog.feature.settings

import androidx.fragment.app.FragmentActivity
import dev.olog.platform.BottomNavigationFragmentTag
import javax.inject.Inject

class FeatureSettingsNavigatorImpl @Inject constructor(
    private val tags: Set<@JvmSuppressWildcards BottomNavigationFragmentTag>,
) : FeatureSettingsNavigator {

    override fun toSettings(activity: FragmentActivity) {
//        superCerealTransition( todo
//            activity = activity,
//            fragment = SettingsFragmentWrapper(),
//            tag = SettingsFragmentWrapper.TAG,
//            tags = tags,
//        )
    }
}