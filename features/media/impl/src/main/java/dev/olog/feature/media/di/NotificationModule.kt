package dev.olog.feature.media.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.interfaces.INotification
import dev.olog.feature.media.notification.NotificationImpl21
import dev.olog.feature.media.notification.NotificationImpl24
import dev.olog.feature.media.notification.NotificationImpl26
import dev.olog.shared.isNougat
import dev.olog.shared.isOreo

@Module
@InstallIn(ServiceComponent::class)
internal object NotificationModule {

    @Provides
    @ServiceScoped
    internal fun provideNotificationImpl(
        notificationImpl26: Lazy<NotificationImpl26>,
        notificationImpl24: Lazy<NotificationImpl24>,
        notificationImpl: Lazy<NotificationImpl21>

    ): INotification {
        return when {
            isOreo() -> notificationImpl26.get()
            isNougat() -> notificationImpl24.get()
            else -> notificationImpl.get()
        }
    }

}