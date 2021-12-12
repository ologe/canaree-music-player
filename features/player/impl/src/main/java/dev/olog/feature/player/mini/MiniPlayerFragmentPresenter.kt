package dev.olog.feature.player.mini

import dev.olog.core.gateway.PlayingItemGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.shared.android.extensions.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragmentPresenter @Inject constructor(
    musicPrefsUseCase: MusicPreferencesGateway,
    private val playingItemGateway: PlayingItemGateway,
) {

    var showTimeLeft = false
    private var currentDuration = 0L

    val skipToNextVisibility = musicPrefsUseCase
        .observeSkipToNextVisibility()
        .asLiveData()

    val skipToPreviousVisibility = musicPrefsUseCase
        .observeSkipToPreviousVisibility()
        .asLiveData()

    fun getMetadata() = playingItemGateway.get()

    fun startShowingLeftTime(show: Boolean, duration: Long) {
        showTimeLeft = show
        currentDuration = duration
    }

    fun observePodcastProgress(flow: Flow<Long>): Flow<Long> {
        return flow.filter { showTimeLeft }
            .map { currentDuration - it }
            .map { TimeUnit.MILLISECONDS.toMinutes(it) }
    }


}