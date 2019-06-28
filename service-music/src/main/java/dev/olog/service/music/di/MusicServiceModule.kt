package dev.olog.service.music.di

import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.Lifecycle
import androidx.media.session.MediaButtonReceiver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.injection.dagger.ServiceContext
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.MusicService
import dev.olog.service.music.queue.QueueManager
import dev.olog.service.music.interfaces.Player
import dev.olog.service.music.interfaces.CustomExoPlayer
import dev.olog.service.music.player.PlayerImpl
import dev.olog.service.music.player.PlayerVolume
import dev.olog.service.music.player.crossfade.CrossFadePlayer
import dev.olog.service.music.interfaces.IPlayerVolume
import dev.olog.service.music.model.PlayerMediaEntity

@Module
abstract class MusicServiceModule {

    @Binds
    @ServiceContext
    internal abstract fun provideContext(instance: MusicService): Context

    @Binds
    internal abstract fun provideService(instance: MusicService): Service

    @Binds
    @PerService
    internal abstract fun provideServiceLifecycle(instance: MusicService): dev.olog.service.music.interfaces.ServiceLifecycleController

    @Binds
    @PerService
    internal abstract fun provideQueue(queue: QueueManager): dev.olog.service.music.interfaces.Queue

    @Binds
    @PerService
    internal abstract fun providePlayer(player: PlayerImpl): Player

    @Binds
    @PerService
    internal abstract fun providePlayerLifecycle(player: Player): dev.olog.service.music.interfaces.PlayerLifecycle

    @Binds
    @PerService
    internal abstract fun providePlayerVolume(volume: PlayerVolume): IPlayerVolume

    @Module
    companion object {
        @Provides
        @JvmStatic
        @ServiceLifecycle
        internal fun provideLifecycle(instance: MusicService): Lifecycle = instance.lifecycle

        @Provides
        @JvmStatic
        @PerService
        internal fun provideMediaSession(instance: MusicService): MediaSessionCompat {
            return MediaSessionCompat(
                    instance,
                    MusicService.TAG,
                    ComponentName(instance, MediaButtonReceiver::class.java),
                    null
            )
        }


        @Provides
        @JvmStatic
        @PerService
        internal fun provideAudioManager(instance: MusicService): AudioManager {
            return instance.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        @Provides
        @JvmStatic
        @PerService
        internal fun provideNotificationManager(instance: MusicService): NotificationManager {
            return instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        @JvmStatic
        internal fun provideToken(mediaSession: MediaSessionCompat): MediaSessionCompat.Token {
            return mediaSession.sessionToken
        }

        @Provides
        @JvmStatic
        internal fun provideMediaController(mediaSession: MediaSessionCompat): MediaControllerCompat {
            return mediaSession.controller
        }

        @Provides
        @PerService
        @JvmStatic
        internal fun providePlayerImpl(
//            simplePlayer: Lazy<SimplePlayer>,
                crossfadePlayer: CrossFadePlayer
        ): CustomExoPlayer<PlayerMediaEntity> {
            return crossfadePlayer
        }
    }

}