package dev.olog.service.music.notification

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.domain.entity.favorite.FavoriteEnum
import dev.olog.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.service.music.player.InternalPlayerState
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ServiceScoped
internal class MusicNotificationManager @Inject constructor(
    schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val notificationImpl: INotification,
    observeFavoriteUseCase: ObserveFavoriteAnimationUseCase,
    internalPlayerState: InternalPlayerState,
) {

    init {
        val isFavoriteFlow = observeFavoriteUseCase()
            .map { it == FavoriteEnum.FAVORITE }

        internalPlayerState.state.combine(isFavoriteFlow) { state, isFavorite ->
            state to isFavorite
        }.mapLatest { (state, isFavorite) ->
            // TODO stop async image loading
            notificationImpl.update(state, isFavorite)
        }.flowOn(schedulers.cpu)
            .onCompletion { stopForeground() }
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun stopForeground() {
        notificationImpl.cancel()
    }

}
