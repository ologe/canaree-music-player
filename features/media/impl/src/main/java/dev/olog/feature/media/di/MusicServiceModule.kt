package dev.olog.feature.media.di

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
import dev.olog.feature.media.MusicService
import dev.olog.feature.media.interfaces.IMaxAllowedPlayerVolume
import dev.olog.feature.media.interfaces.IPlayer
import dev.olog.feature.media.interfaces.IPlayerDelegate
import dev.olog.feature.media.interfaces.IPlayerLifecycle
import dev.olog.feature.media.interfaces.IQueue
import dev.olog.feature.media.interfaces.IServiceLifecycleController
import dev.olog.feature.media.api.model.PlayerMediaEntity
import dev.olog.feature.media.player.PlayerImpl
import dev.olog.feature.media.player.PlayerVolume
import dev.olog.feature.media.player.crossfade.CrossFadePlayerSwitcher
import dev.olog.feature.media.queue.QueueManager
import dev.olog.shared.extension.findInContext

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
        internal fun provideServiceLifecycle(service: Service): IServiceLifecycleController {
            return service.findInContext()
        }

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
    }

}