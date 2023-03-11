package dev.olog.feature.media.impl.di

import android.app.Service
import android.content.ComponentName
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.session.MediaButtonReceiver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.impl.interfaces.IMaxAllowedPlayerVolume
import dev.olog.feature.media.impl.interfaces.IPlayer
import dev.olog.feature.media.impl.interfaces.IPlayerDelegate
import dev.olog.feature.media.impl.interfaces.IPlayerLifecycle
import dev.olog.feature.media.impl.interfaces.IQueue
import dev.olog.feature.media.impl.interfaces.IServiceLifecycleController
import dev.olog.feature.media.impl.MusicService
import dev.olog.feature.media.impl.model.PlayerMediaEntity
import dev.olog.feature.media.impl.player.PlayerImpl
import dev.olog.feature.media.impl.player.PlayerVolume
import dev.olog.feature.media.impl.player.crossfade.CrossFadePlayerSwitcher
import dev.olog.feature.media.impl.queue.QueueManager

@Module
@InstallIn(ServiceComponent::class)
abstract class MusicServiceModule {

    @Binds
    @ServiceScoped
    abstract fun provideQueue(queue: QueueManager): IQueue

    @Binds
    @ServiceScoped
    abstract fun providePlayer(player: PlayerImpl): IPlayer

    @Binds
    @ServiceScoped
    abstract fun providePlayerLifecycle(player: IPlayer): IPlayerLifecycle

    @Binds
    @ServiceScoped
    abstract fun providePlayerVolume(volume: PlayerVolume): IMaxAllowedPlayerVolume

    @Binds
    @ServiceScoped
    abstract fun providePlayerImpl(impl: CrossFadePlayerSwitcher): IPlayerDelegate<PlayerMediaEntity>

    companion object {

        @Provides
        fun provideServiceLifecycleController(instance: Service): IServiceLifecycleController {
            require(instance is IServiceLifecycleController)
            return instance
        }

        @Provides
        @ServiceScoped
        fun provideMediaSession(instance: Service): MediaSessionCompat {
            return MediaSessionCompat(
                instance,
                MusicService.TAG,
                ComponentName(instance, MediaButtonReceiver::class.java),
                null
            )
        }
    }

}