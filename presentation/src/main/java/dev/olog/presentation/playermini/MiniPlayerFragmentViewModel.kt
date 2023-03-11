package dev.olog.presentation.playermini

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.shared.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MiniPlayerFragmentViewModel @Inject constructor(
    private val musicPrefsUseCase: MusicPreferencesGateway
) : ViewModel() {

    var showTimeLeft = false
    private var currentDuration = 0L

    val skipToNextVisibility = musicPrefsUseCase
        .observeSkipToNextVisibility()
        .asLiveData()

    val skipToPreviousVisibility = musicPrefsUseCase
        .observeSkipToPreviousVisibility()
        .asLiveData()

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