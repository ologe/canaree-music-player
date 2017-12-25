package dev.olog.msc

import dagger.Module
import dagger.Provides
import dev.olog.music_service.MusicService
import dev.olog.music_service.interfaces.ActivityClass
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.service_music.MusicServiceBinder

@Module
class MusicServiceBinderModule {

    @Provides
    internal fun providerMusicServiceBinder(): MusicServiceBinder {
        return object : MusicServiceBinder {
            override fun get() = MusicService::class.java
        }
    }

    @Provides
    internal fun provideActivityClass(): ActivityClass {
        return object : ActivityClass {
            override fun get(): Class<*> = MainActivity::class.java
        }
    }

}