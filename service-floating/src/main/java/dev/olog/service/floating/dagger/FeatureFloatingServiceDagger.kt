package dev.olog.service.floating.dagger

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import dev.olog.navigation.dagger.NavigationIntentKey
import dev.olog.navigation.destination.NavigationIntent
import dev.olog.service.floating.FloatingWindowService

@Module
@InstallIn(ApplicationComponent::class)
internal object FeatureFloatingServiceDagger {

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.FLOATING_SERVICE)
    fun providePlayIntent(@ApplicationContext context: Context): Intent {
        return Intent(context, FloatingWindowService::class.java)
    }

}