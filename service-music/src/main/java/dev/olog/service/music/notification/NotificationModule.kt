package dev.olog.service.music.notification

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.interfaces.INotification
import dev.olog.platform.isNougat
import dev.olog.platform.isOreo

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
            isOreo() -> notificationImpl26.get()
            isNougat() -> notificationImpl24.get()
            else -> notificationImpl.get()
        }
    }

}