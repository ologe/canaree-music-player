package dev.olog.lib.analytics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.lib.analytics.tracker.FirebaseTracker
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    internal abstract fun provideTrackerFacade(impl: FirebaseTracker): TrackerFacade

}