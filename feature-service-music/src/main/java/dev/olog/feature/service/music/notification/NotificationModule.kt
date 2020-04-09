package dev.olog.feature.service.music.notification

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.FeatureScope
import dev.olog.core.isNougat
import dev.olog.core.isOreo
import dev.olog.feature.service.music.interfaces.INotification

@Module
internal object NotificationModule {

    @Provides
    @FeatureScope
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