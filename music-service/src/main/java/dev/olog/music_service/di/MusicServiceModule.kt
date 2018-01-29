package dev.olog.music_service.di

import android.app.NotificationManager
import android.app.Service
import android.app.UiModeManager
import android.arch.lifecycle.Lifecycle
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import dagger.Module
import dagger.Provides
import dev.olog.music_service.MusicService
import dev.olog.music_service.QueueManager
import dev.olog.music_service.interfaces.Player
import dev.olog.music_service.interfaces.PlayerLifecycle
import dev.olog.music_service.interfaces.Queue
import dev.olog.music_service.interfaces.ServiceLifecycleController
import dev.olog.music_service.player.PlayerImpl
import dev.olog.music_service.player.PlayerVolume
import dev.olog.music_service.volume.IPlayerVolume
import dev.olog.shared_android.extension.notificationManager

@Module(includes = arrayOf(MusicServiceModule.Binds::class))
class MusicServiceModule(
        private val service: MusicService
) {

    @Provides
    @ServiceContext
    internal fun provideContext(): Context = service

    @Provides
    internal fun provideService(): Service = service

    @Provides
    @PerService
    internal fun provideServiceLifecycle(): ServiceLifecycleController = service


    @Provides
    @ServiceLifecycle
    internal fun provideLifecycle(): Lifecycle = service.lifecycle

    @Provides
    @PerService
    internal fun provideMediaSession(): MediaSessionCompat {
        return MediaSessionCompat(service, MusicService.TAG,
                ComponentName(service, MediaButtonReceiver::class.java),
                null)
    }

    @Provides
    @PerService
    internal fun provideAudioManager(): AudioManager {
        return service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @PerService
    internal fun provideUiModeManager(): UiModeManager {
        return service.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    }

    @Provides
    @PerService
    internal fun provideNotificationManager(): NotificationManager {
        return service.notificationManager
    }

    @Provides
    internal fun provideToken(mediaSession: MediaSessionCompat): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }

    @Provides
    internal fun provideMediaController(mediaSession: MediaSessionCompat): MediaControllerCompat {
        return mediaSession.controller
    }

    @Module
    abstract class Binds {

        @dagger.Binds
        @PerService
        abstract fun provideQueue(queue: QueueManager): Queue

        @dagger.Binds
        @PerService
        abstract fun providePlayer(player: PlayerImpl): Player

        @dagger.Binds
        @PerService
        abstract fun providePlayerLifecycle(player: PlayerImpl): PlayerLifecycle

        @dagger.Binds
        @PerService
        abstract fun providePlayerVolume(volume: PlayerVolume): IPlayerVolume

    }

}