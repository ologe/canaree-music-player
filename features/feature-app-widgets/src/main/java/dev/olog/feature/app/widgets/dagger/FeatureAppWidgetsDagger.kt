package dev.olog.feature.app.widgets.dagger

import android.appwidget.AppWidgetProvider
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.feature.app.widgets.WidgetColored
import dev.olog.navigation.dagger.WidgetKey
import dev.olog.navigation.screens.Widgets

class FeatureAppWidgetsDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector
        internal abstract fun provideWidgets(): WidgetColored

        companion object {

            @Provides
            @IntoMap
            @WidgetKey(Widgets.DEFAULT)
            internal fun provideWidget(): Class<out AppWidgetProvider> {
                return WidgetColored::class.java
            }

        }

    }

}