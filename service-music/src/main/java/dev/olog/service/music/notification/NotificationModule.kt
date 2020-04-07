package dev.olog.service.music.notification

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.INotification
import dev.olog.core.isNougat
import dev.olog.core.isOreo

@Module
internal object NotificationModule {

    @Provides
    @PerService
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