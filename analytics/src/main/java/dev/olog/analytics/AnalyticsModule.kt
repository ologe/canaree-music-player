package dev.olog.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.analytics.tracker.FirebaseTracker

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class AnalyticsModule {

    @Binds
    @Reusable
    internal abstract fun provideTrackerFacade(impl: FirebaseTracker): TrackerFacade

    companion object {

        @Provides
        internal fun provideFirebaseAnalytics(): FirebaseAnalytics {
            return Firebase.analytics
        }

    }

}