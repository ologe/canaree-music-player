package dev.olog.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.analytics.tracker.FirebaseTracker
import dev.olog.shared.ApplicationContext
import javax.inject.Singleton

@Module
abstract class AnalyticsModule {

    @Binds
    @Singleton
    internal abstract fun provideTrackerFacade(impl: FirebaseTracker): TrackerFacade

    companion object {
        @Provides
        internal fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
            return FirebaseAnalytics.getInstance(context)
        }
    }

}