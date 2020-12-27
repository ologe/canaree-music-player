package dev.olog.navigation.dagger

import dagger.MapKey
import dev.olog.navigation.destination.FragmentScreen

@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class FragmentScreenKey(val value: FragmentScreen)