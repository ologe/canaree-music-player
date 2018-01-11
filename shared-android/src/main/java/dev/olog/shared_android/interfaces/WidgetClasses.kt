package dev.olog.shared_android.interfaces

import android.appwidget.AppWidgetProvider

interface WidgetClasses {

    fun get(): List<Class<out AppWidgetProvider>>

}