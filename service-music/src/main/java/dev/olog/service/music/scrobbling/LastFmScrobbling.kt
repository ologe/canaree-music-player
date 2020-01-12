package dev.olog.service.music.scrobbling

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.interactor.ObserveLastFmUserCredentials
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MetadataEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LastFmScrobbling @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: IPlayerLifecycle,
    private val lastFmService: LastFmService

) : DefaultLifecycleObserver,
    IPlayerLifecycle.Listener,
    CoroutineScope by MainScope() {

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)

        launch {
            observeLastFmUserCredentials()
                .filter { it.username.isNotBlank() }
                .collect { lastFmService::tryAuthenticate }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
        lastFmService.dispose()
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        lastFmService.scrobble(metadata.entity)
    }

}