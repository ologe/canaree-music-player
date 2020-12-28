package dev.olog.presentation.playermini

import dev.olog.core.prefs.MusicPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.milliseconds

class MiniPlayerFragmentPresenter @Inject constructor(
    private val musicPrefsUseCase: MusicPreferencesGateway

) {

    var showTimeLeft = false
    private var currentDuration: Duration = 0.milliseconds

    val skipToNextVisibility = musicPrefsUseCase
        .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
        .observeSkipToPreviousVisibility()

    fun getMetadata() = musicPrefsUseCase.getLastMetadata()

    fun startShowingLeftTime(show: Boolean, duration: Duration) {
        showTimeLeft = show
        currentDuration = duration
    }

    @Deprecated("rewrite")
    fun observePodcastProgress(flow: Flow<Long>): Flow<Long> {
        return flow.filter { showTimeLeft }
            .map { currentDuration.toLongMilliseconds() - it }
            .map { TimeUnit.MILLISECONDS.toMinutes(it) }
    }


}