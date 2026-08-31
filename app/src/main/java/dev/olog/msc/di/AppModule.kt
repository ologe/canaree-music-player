package dev.olog.msc.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesImpl

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideNotificationManager(app: Application): NotificationManager {
        return app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // TODO move to a different place
    @Provides
    internal fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway = impl

}