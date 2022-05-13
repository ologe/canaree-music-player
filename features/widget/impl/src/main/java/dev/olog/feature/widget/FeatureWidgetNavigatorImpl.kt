package dev.olog.feature.widget

import android.appwidget.AppWidgetProvider
import javax.inject.Inject

class FeatureWidgetNavigatorImpl @Inject constructor(

) : FeatureWidgetNavigator {

    override fun widgetClasses(): Set<Class<out AppWidgetProvider>> {
        return setOf(
            WidgetColored::class.java
        )
    }
}