package dev.olog.feature.player.mini

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.feature.media.api.MusicPreferencesGateway
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

    val skipToPreviousVisibility = musicPrefsUseCase
        .observeSkipToPreviousVisibility()

    fun getMetadata(): String = TODO()//musicPrefsUseCase.getLastMetadata()

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