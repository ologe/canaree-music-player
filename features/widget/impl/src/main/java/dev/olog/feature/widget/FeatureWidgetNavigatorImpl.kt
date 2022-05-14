package dev.olog.feature.widget

import android.appwidget.AppWidgetProvider
import dev.olog.feature.widget.api.FeatureWidgetNavigator
import javax.inject.Inject

class FeatureWidgetNavigatorImpl @Inject constructor(

) : FeatureWidgetNavigator {

    override fun widgetClasses(): Set<Class<out AppWidgetProvider>> {
        return setOf(
            WidgetColored::class.java
        )
    }
}