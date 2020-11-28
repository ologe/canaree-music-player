package dev.olog.service.music.notification

import android.app.Service
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.entity.favorite.FavoriteEnum.FAVORITE
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.service.music.player.InternalPlayerState
import dev.olog.shared.mapWithLatest
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ServiceScoped
internal class MusicNotificationManager @Inject constructor(
    private val service: Service,
    schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val notificationImpl: INotification,
    private val observeFavoriteUseCase: ObserveFavoriteAnimationUseCase,
    internalPlayerState: InternalPlayerState,
) {

    init {
        internalPlayerState.state
            .mapWithLatest(initialValue = null, mapper = this::update)
            .flowOn(schedulers.cpu)
            .onCompletion { stopForeground() } // stop foreground on service destroyed
            .launchIn(lifecycleOwner.lifecycleScope)

        observeFavoriteUseCase()
            .map { it == FAVORITE }
            .mapLatest(notificationImpl::updateFavorite)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private suspend fun update(
        old: InternalPlayerState.Data?,
        new: InternalPlayerState.Data
    ) {
        if (new.prepare) {
            notificationImpl.prepare(new, observeFavoriteUseCase().first() == FAVORITE)
            return
        }
        requireNotNull(old) // should be null only when new.prepare == true

        if (new.isDifferentMetadata(old)) {
            require(new.isSameState(old))
            notificationImpl.updateMetadata(new.entity)
            return
        }
        if (new.isDifferentState(old)) {
            require(new.isSameMetadata(old))
            notificationImpl.updateState(new.isPlaying, new.bookmark, new.entity.duration)
            return
        }
    }

    private fun stopForeground() {
        service.stopForeground(true)
        notificationImpl.cancel()
    }

}
