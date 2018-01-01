package dev.olog.floating_info.music_service

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import dagger.Module
import dagger.Provides
import dev.olog.floating_info.di.PerService
import dev.olog.shared.ApplicationContext
import dev.olog.shared_android.interfaces.MusicServiceClass
import dev.olog.shared_android.music_service.RxMusicServiceConnectionCallback

@Module
class MusicServiceModule {

    @Provides
    @PerService // todo nope
    internal fun provideRxMusicServiceConnectionCallback() : RxMusicServiceConnectionCallback {
        return RxMusicServiceConnectionCallback()
    }

    @Provides
    @PerService
    internal fun provideMediaBrowser(
            serviceBinder: MusicServiceClass,
            @ApplicationContext context: Context,
            rxConnectionCallback: RxMusicServiceConnectionCallback): MediaBrowserCompat {

        return MediaBrowserCompat(context,
                ComponentName(context, serviceBinder.get()),
                rxConnectionCallback.get(), null)
    }

}