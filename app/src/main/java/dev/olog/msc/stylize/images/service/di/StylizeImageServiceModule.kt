package dev.olog.msc.stylize.images.service.di

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.stylize.images.service.StylizeImageService

@Module
class StylizeImageServiceModule(private val service: StylizeImageService) {

    @Provides
    fun provideNotificationManger() : NotificationManager {
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}