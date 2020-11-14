package dev.olog.service.music.dagger

import android.app.Service
import android.content.ComponentName
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleOwner
import androidx.media.session.MediaButtonReceiver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.MusicService
import dev.olog.service.music.interfaces.*
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
    internal abstract fun provideServiceLifecycle(instance: MusicService): IServiceLifecycleController

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
        internal fun provideMediaSession(instance: MusicService): MediaSessionCompat {
            return MediaSessionCompat(
                instance,
                MusicService.TAG,
                ComponentName(instance, MediaButtonReceiver::class.java),
                null
            )
        }

        @Provides
        fun provideMusicService(service: Service): MusicService {
            require(service is MusicService)
            return service
        }

        @Provides
        fun provideLifecycleOwner(service: MusicService): LifecycleOwner = service

    }

}