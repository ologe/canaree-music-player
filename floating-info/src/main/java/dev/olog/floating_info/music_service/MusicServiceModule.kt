package dev.olog.floating_info.music_service

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import dagger.Module
import dagger.Provides
import dev.olog.floating_info.di.PerService
import dev.olog.shared.ApplicationContext
import dev.olog.shared_android.interfaces.MusicServiceClass
import dev.olog.shared_android.music_service.IRxMusicServiceConnectionCallback
import dev.olog.shared_android.music_service.IRxMusicServiceControllerCallback
import dev.olog.shared_android.music_service.RxMusicServiceConnectionCallback
import dev.olog.shared_android.music_service.RxMusicServiceControllerCallback

@Module
class MusicServiceModule {

    @Provides
    @PerService
    internal fun provideMediaBrowser(
            serviceBinder: MusicServiceClass,
            @ApplicationContext context: Context,
            rxConnectionCallback: IRxMusicServiceConnectionCallback): MediaBrowserCompat {

        return MediaBrowserCompat(context,
                ComponentName(context, serviceBinder.get()),
                rxConnectionCallback.get(), null)
    }

    @Provides
    @PerService
    internal fun provideRxConnectionCallback(connectionCallback: RxMusicServiceConnectionCallback)
            : IRxMusicServiceConnectionCallback {
        return connectionCallback
    }

    @Provides
    @PerService
    internal fun provideRxControllerCallback(controllerCallback: RxMusicServiceControllerCallback)
            : IRxMusicServiceControllerCallback {
        return controllerCallback
    }

}