package dev.olog.feature.remote.widget.dagger

import android.appwidget.AppWidgetProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.remote.widget.WidgetColored
import dev.olog.navigation.dagger.RemoteWidgetKey
import dev.olog.navigation.destination.RemoteWidget

@Module
@InstallIn(ApplicationComponent::class)
object FeatureRemoteWidgetNavigation {

    @Provides
    @IntoMap
    @RemoteWidgetKey(RemoteWidget.DEFAULT)
    fun provideDefault(): Class<out AppWidgetProvider> = WidgetColored::class.java

}