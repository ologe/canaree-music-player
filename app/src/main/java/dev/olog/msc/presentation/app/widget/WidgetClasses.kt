package dev.olog.msc.presentation.app.widget

import android.appwidget.AppWidgetProvider

interface WidgetClasses {

    fun get(): List<Class<out AppWidgetProvider>>

}