package dev.olog.service.music.scrobbling

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.coroutines.MainScope
import dev.olog.domain.interactor.lastfm.ObserveLastFmUserCredentials
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MetadataEntity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class LastFmScrobbling @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: IPlayerLifecycle,
    private val lastFmService: LastFmService

) : DefaultLifecycleObserver,
    IPlayerLifecycle.Listener {

    private val scope by MainScope()

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)

        observeLastFmUserCredentials()
            .filter { it.username.isNotBlank() }
            .onEach { lastFmService.tryAuthenticate(it) }
            .launchIn(scope)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        scope.cancel()
        lastFmService.dispose()
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        lastFmService.scrobble(metadata.entity)
    }

}