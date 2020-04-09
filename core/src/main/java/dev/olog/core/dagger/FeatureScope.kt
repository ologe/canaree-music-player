package dev.olog.core.dagger

import javax.inject.Scope

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Scope
annotation class FeatureScope