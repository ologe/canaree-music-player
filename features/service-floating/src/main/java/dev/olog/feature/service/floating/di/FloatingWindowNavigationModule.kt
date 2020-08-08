package dev.olog.feature.service.floating.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import dev.olog.feature.service.floating.FloatingWindowService
import dev.olog.navigation.dagger.NavigationIntentKey
import dev.olog.navigation.screens.NavigationIntent

@Module
@InstallIn(ApplicationComponent::class)
object FloatingWindowNavigationModule {

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.SERVICE_FLOATING)
    fun provideIntent(@ApplicationContext context: Context): Intent {
        return Intent(context, FloatingWindowService::class.java)
    }

}