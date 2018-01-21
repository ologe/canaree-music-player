package dev.olog.presentation.activity_main.di

import android.arch.lifecycle.Lifecycle
import android.content.ComponentName
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.service_music.MediaControllerProvider
import dev.olog.shared.ApplicationContext
import dev.olog.shared_android.interfaces.MusicServiceClass
import dev.olog.shared_android.music_service.IRxMusicServiceConnectionCallback
import dev.olog.shared_android.music_service.IRxMusicServiceControllerCallback
import dev.olog.shared_android.music_service.RxMusicServiceConnectionCallback
import dev.olog.shared_android.music_service.RxMusicServiceControllerCallback

@Module
class MainActivityModule(
        private val activity: MainActivity
) {

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    @ActivityLifecycle
    internal fun provideLifecycle() : Lifecycle = activity.lifecycle

    @Provides
    internal fun provideFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }

    @Provides
    internal fun provideActivity(): AppCompatActivity = activity

    @Provides
    internal fun provideFragmentActivity() : FragmentActivity = activity

    @Provides
    internal fun provideMusicControllerProvider(): MediaControllerProvider = activity

    @Provides
    @PerActivity
    internal fun provideMediaBrowser(
            @ApplicationContext context: Context,
            serviceBinder: MusicServiceClass,
            rxConnectionCallback: IRxMusicServiceConnectionCallback): MediaBrowserCompat {

        return MediaBrowserCompat(context,
                ComponentName(context, serviceBinder.get()),
                rxConnectionCallback.get(), null)
    }

    @Provides
    @PerActivity
    internal fun provideRxConnectionCallback(connectionCallback: RxMusicServiceConnectionCallback)
            : IRxMusicServiceConnectionCallback {
        return connectionCallback
    }

    @Provides
    @PerActivity
    internal fun provideRxControllerCallback(controllerCallback: RxMusicServiceControllerCallback)
            : IRxMusicServiceControllerCallback {
        return controllerCallback
    }

}