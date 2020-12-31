package dev.olog.navigation.dagger

import android.appwidget.AppWidgetProvider
import dagger.MapKey
import dev.olog.navigation.destination.RemoteWidget
import javax.inject.Provider

@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class RemoteWidgetKey(val value: RemoteWidget)

typealias RemoteWidgets = Map<RemoteWidget, @JvmSuppressWildcards Provider<Class<out AppWidgetProvider>>>