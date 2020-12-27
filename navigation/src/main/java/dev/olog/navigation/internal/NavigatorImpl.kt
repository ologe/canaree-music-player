package dev.olog.navigation.internal

import androidx.fragment.app.Fragment
import dev.olog.navigation.Navigator
import dev.olog.navigation.destination.FragmentScreen
import javax.inject.Inject
import javax.inject.Provider

internal class NavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : BaseNavigator(),
    Navigator {

    override fun toFirstAccess() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.ONBOARDING]?.get()
        val tag = FragmentScreen.ONBOARDING.tag
        replaceFragment(activity, fragment, tag, android.R.id.content, forced = true)
    }
}