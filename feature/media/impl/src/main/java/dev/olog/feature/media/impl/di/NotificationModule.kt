package dev.olog.feature.media.impl.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.impl.interfaces.INotification
import dev.olog.feature.media.impl.notification.NotificationImpl21
import dev.olog.feature.media.impl.notification.NotificationImpl24
import dev.olog.feature.media.impl.notification.NotificationImpl26
import dev.olog.platform.BuildVersion

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @Provides
    @ServiceScoped
    fun provideNotificationImpl(
        notificationImpl26: Lazy<NotificationImpl26>,
        notificationImpl24: Lazy<NotificationImpl24>,
        notificationImpl: Lazy<NotificationImpl21>

    ): INotification {
        return when {
            BuildVersion.isOreo() -> notificationImpl26.get()
            BuildVersion.isNougat() -> notificationImpl24.get()
            else -> notificationImpl.get()
        }
    }

}