package dev.olog.feature.widget

import android.appwidget.AppWidgetProvider

interface FeatureWidgetNavigator {

    fun widgetClasses(): Set<Class<out AppWidgetProvider>>

}