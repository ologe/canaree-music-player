package dev.olog.navigation.dagger

import dagger.MapKey
import dev.olog.navigation.screens.NavigationIntent

@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class NavigationIntentKey(val value: NavigationIntent)