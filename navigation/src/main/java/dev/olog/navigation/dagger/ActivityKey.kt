package dev.olog.navigation.dagger

import dagger.MapKey
import dev.olog.navigation.screens.Activities

@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class ActivityKey(val value: Activities)