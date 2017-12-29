package dev.olog.msc

import android.app.Service
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.floating_info.FloatingInfoService
import dev.olog.music_service.MusicService
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.shared_android.interfaces.FloatingInfoServiceClass
import dev.olog.shared_android.interfaces.MainActivityClass
import dev.olog.shared_android.interfaces.MusicServiceClass

@Module
class SharedClassModule {

    @Provides
    internal fun provideMainActivityClass(): MainActivityClass {
        return object : MainActivityClass {
            override fun get(): Class<out AppCompatActivity> {
                return MainActivity::class.java
            }
        }
    }

    @Provides
    internal fun provideMusicServiceClass(): MusicServiceClass {
        return object : MusicServiceClass {
            override fun get(): Class<out MediaBrowserServiceCompat> {
                return MusicService::class.java
            }
        }
    }

    @Provides
    internal fun provideFloatingInfoClass(): FloatingInfoServiceClass {
        return object : FloatingInfoServiceClass {
            override fun get(): Class<out Service> {
                return FloatingInfoService::class.java
            }
        }
    }

}