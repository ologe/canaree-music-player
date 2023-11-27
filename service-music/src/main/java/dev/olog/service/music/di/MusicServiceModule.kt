package dev.olog.service.music.di

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
import dev.olog.service.music.MusicService
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.interfaces.IQueue
import dev.olog.service.music.interfaces.IServiceLifecycleController
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.player.PlayerImpl
import dev.olog.service.music.player.PlayerVolume
import dev.olog.service.music.player.crossfade.CrossFadePlayerSwitcher
import dev.olog.service.music.queue.QueueManager

@Module
@InstallIn(ServiceComponent::class)
abstract class MusicServiceModule {

    @Binds
    @ServiceScoped
    internal abstract fun provideQueue(queue: QueueManager): IQueue

    @Binds
    @ServiceScoped
    internal abstract fun providePlayer(player: PlayerImpl): IPlayer

    @Binds
    @ServiceScoped
    internal abstract fun providePlayerLifecycle(player: IPlayer): IPlayerLifecycle

    @Binds
    @ServiceScoped
    internal abstract fun providePlayerVolume(volume: PlayerVolume): IMaxAllowedPlayerVolume

    @Binds
    @ServiceScoped
    internal abstract fun providePlayerImpl(impl: CrossFadePlayerSwitcher): IPlayerDelegate<PlayerMediaEntity>

    companion object {

        @Provides
        @ServiceScoped
        internal fun provideMediaSession(service: Service): MediaSessionCompat {
            return MediaSessionCompat(
                service,
                MusicService.TAG,
                ComponentName(service, MediaButtonReceiver::class.java),
                null
            )
        }

        @Provides
        internal fun provideServiceLifecycle(instance: Service): IServiceLifecycleController {
            require(instance is IServiceLifecycleController)
            return instance
        }

    }

}