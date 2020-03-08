package dev.olog.presentation.playermini

import dev.olog.core.prefs.MusicPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragmentPresenter @Inject constructor(
    private val musicPrefsUseCase: MusicPreferencesGateway

) {

    var showTimeLeft = false
    private var currentDuration = 0L

    val skipToNextVisibility: Flow<Boolean> = musicPrefsUseCase
        .observeSkipToNextVisibility()

    val skipToPreviousVisibility: Flow<Boolean> = musicPrefsUseCase
        .observeSkipToPreviousVisibility()

    fun getMetadata() = musicPrefsUseCase.getLastMetadata()

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