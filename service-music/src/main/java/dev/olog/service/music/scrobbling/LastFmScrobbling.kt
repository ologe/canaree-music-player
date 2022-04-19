package dev.olog.service.music.scrobbling

import dev.olog.core.ServiceScope
import dev.olog.core.interactor.ObserveLastFmUserCredentials
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MetadataEntity
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class LastFmScrobbling @Inject constructor(
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: IPlayerLifecycle,
    private val lastFmService: LastFmService,
    private val serviceScope: ServiceScope,
) : IPlayerLifecycle.Listener {

    init {
        playerLifecycle.addListener(this)

        observeLastFmUserCredentials()
            .filter { it.username.isNotBlank() }
            .onEach { lastFmService.tryAuthenticate(it) }
            .launchIn(serviceScope)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        lastFmService.scrobble(metadata.entity)
    }

}