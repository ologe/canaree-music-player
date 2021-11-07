package dev.olog.msc.main

import androidx.fragment.app.FragmentActivity
import dev.olog.feature.base.superCerealTransition
import dev.olog.feature.main.FeatureMainNavigator
import dev.olog.msc.settings.SettingsFragmentWrapper
import javax.inject.Inject

class FeatureMainNavigatorImpl @Inject constructor(

) : FeatureMainNavigator {

    override fun toSettings(activity: FragmentActivity) {
        superCerealTransition(
            activity = activity,
            fragment = SettingsFragmentWrapper(),
            tag = SettingsFragmentWrapper.TAG
        )
    }
}