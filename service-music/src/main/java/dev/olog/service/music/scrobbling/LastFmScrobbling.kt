package dev.olog.service.music.scrobbling

import android.app.Service
import dev.olog.core.interactor.ObserveLastFmUserCredentials
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MetadataEntity
import dev.olog.shared.android.extensions.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LastFmScrobbling @Inject constructor(
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