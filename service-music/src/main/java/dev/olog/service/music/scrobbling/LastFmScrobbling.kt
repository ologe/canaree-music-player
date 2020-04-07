package dev.olog.service.music.scrobbling

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.olog.domain.interactor.lastfm.ObserveLastFmUserCredentials
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MetadataEntity
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class LastFmScrobbling @Inject constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: IPlayerLifecycle,
    private val lastFmService: LastFmService

) : IPlayerLifecycle.Listener {

    init {
        playerLifecycle.addListener(this)

        observeLastFmUserCredentials()
            .filter { it.username.isNotBlank() }
            .onEach { lastFmService.tryAuthenticate(it) }
            .launchIn(lifecycle.coroutineScope)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        lastFmService.scrobble(metadata.entity)
    }

}