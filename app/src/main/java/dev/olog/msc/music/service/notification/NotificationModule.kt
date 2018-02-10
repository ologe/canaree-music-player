package dev.olog.msc.music.service.notification

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.PerService
import dev.olog.msc.music.service.interfaces.INotification
import dev.olog.shared_android.isNougat
import dev.olog.shared_android.isOreo

@Module
class NotificationModule {

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