package dev.olog.service.music.notification

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.INotification
import dev.olog.shared.utils.isNougat
import dev.olog.shared.utils.isOreo

@Module
internal class NotificationModule {

    @Provides
    @PerService
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