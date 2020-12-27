package dev.olog.navigation.internal

import androidx.fragment.app.Fragment
import dev.olog.navigation.Navigator
import dev.olog.navigation.destination.FragmentScreen
import javax.inject.Inject
import javax.inject.Provider

internal class NavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : Navigator {



}