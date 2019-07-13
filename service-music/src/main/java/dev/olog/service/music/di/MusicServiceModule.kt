package dev.olog.service.music.di

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.Lifecycle
import androidx.media.session.MediaButtonReceiver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceContext
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.MusicService
import dev.olog.service.music.interfaces.*
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.player.PlayerImpl
import dev.olog.service.music.player.PlayerVolume
import dev.olog.service.music.player.crossfade.CrossFadePlayer
import dev.olog.service.music.queue.QueueManager

@Module
abstract class MusicServiceModule {

    @Binds
    @ServiceContext
    internal abstract fun provideContext(instance: MusicService): Context

    @Binds
    internal abstract fun provideService(instance: MusicService): Service

    @Binds
    @PerService
    internal abstract fun provideServiceLifecycle(instance: MusicService): ServiceLifecycleController

    @Binds
    @PerService
    internal abstract fun provideQueue(queue: QueueManager): Queue

    @Binds
    @PerService
    internal abstract fun providePlayer(player: PlayerImpl): Player

    @Binds
    @PerService
    internal abstract fun providePlayerLifecycle(player: Player): PlayerLifecycle

    @Binds
    @PerService
    internal abstract fun providePlayerVolume(volume: PlayerVolume): IMaxAllowedPlayerVolume

    @Binds
    @PerService
    internal abstract fun providePlayerImpl(impl: CrossFadePlayer): CustomExoPlayer<PlayerMediaEntity>

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
    }

}