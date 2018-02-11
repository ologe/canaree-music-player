package dev.olog.msc.stylize.images.service.di

import android.app.NotificationManager
import androidx.content.systemService
import dagger.Module
import dagger.Provides
import dev.olog.msc.stylize.images.service.StylizeImageService

@Module
class StylizeImageServiceModule(private val service: StylizeImageService) {

    @Provides
    fun provideNotificationManger() : NotificationManager {
        return service.systemService()
    }

}