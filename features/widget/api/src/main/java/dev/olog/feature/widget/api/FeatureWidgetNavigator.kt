package dev.olog.feature.widget.api

import android.appwidget.AppWidgetProvider

interface FeatureWidgetNavigator {

    fun widgetClasses(): Set<Class<out AppWidgetProvider>>

}