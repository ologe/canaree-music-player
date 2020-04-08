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
import dev.olog.core.dagger.FeatureScope
import dev.olog.core.dagger.ServiceContext
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.service.music.MusicService
import dev.olog.service.music.interfaces.*
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.player.PlayerImpl
import dev.olog.service.music.player.PlayerVolume
import dev.olog.service.music.player.crossfade.CrossFadePlayerSwitcher
import dev.olog.service.music.queue.QueueManager

@Module
abstract class MusicServiceModule {

    @Binds
    @ServiceContext
    internal abstract fun provideContext(instance: MusicService): Context

    @Binds
    internal abstract fun provideService(instance: MusicService): Service

    @Binds
    @FeatureScope
    internal abstract fun provideServiceLifecycle(instance: MusicService): IServiceLifecycleController

    @Binds
    @FeatureScope
    internal abstract fun provideQueue(queue: QueueManager): IQueue

    @Binds
    @FeatureScope
    internal abstract fun providePlayer(player: PlayerImpl): IPlayer

    @Binds
    @FeatureScope
    internal abstract fun providePlayerLifecycle(player: IPlayer): IPlayerLifecycle

    @Binds
    @FeatureScope
    internal abstract fun providePlayerVolume(volume: PlayerVolume): IMaxAllowedPlayerVolume

    @Binds
    @FeatureScope
    internal abstract fun providePlayerImpl(impl: CrossFadePlayerSwitcher): IPlayerDelegate<PlayerMediaEntity>

    companion object {

        @Provides
        @ServiceLifecycle
        internal fun provideLifecycle(instance: MusicService): Lifecycle = instance.lifecycle

        @Provides
        @FeatureScope
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