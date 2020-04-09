package dev.olog.navigation.dagger

import dagger.MapKey
import dev.olog.navigation.screens.FragmentScreen

@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class FragmentScreenKey(val value: FragmentScreen)