package dev.olog.feature.media.impl.scrobbling

import android.app.Service
import dev.olog.core.interactor.ObserveLastFmUserCredentials
import dev.olog.feature.media.impl.interfaces.IPlayerLifecycle
import dev.olog.feature.media.impl.model.MetadataEntity
import dev.olog.platform.extension.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class LastFmScrobbling @Inject constructor(
    private val service: Service,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    private val lastFmService: LastFmService
) : IPlayerLifecycle.Listener {

    private var job: Job? = null

    init {
        service.lifecycleScope.launch {
            observeLastFmUserCredentials()
                .filter { it.username.isNotBlank() }
                .collect { lastFmService.tryAuthenticate(it) }
        }
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        job?.cancel()
        job = service.lifecycleScope.launch {
            lastFmService.scrobble(metadata.entity)
        }
    }

}