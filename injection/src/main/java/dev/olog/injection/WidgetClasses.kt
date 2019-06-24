package dev.olog.injection

import android.appwidget.AppWidgetProvider

interface WidgetClasses {

    fun get(): List<Class<out AppWidgetProvider>>

}