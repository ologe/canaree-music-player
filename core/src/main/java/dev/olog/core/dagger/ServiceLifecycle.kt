package dev.olog.core.dagger

import javax.inject.Qualifier

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Qualifier
annotation class ServiceLifecycle
