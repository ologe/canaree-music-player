package dev.olog.domain.interactor.music_service

import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.Flowable
import javax.inject.Inject

class ToggleSkipToNextVisibilityUseCase @Inject constructor(
        private val preferences: MusicPreferencesGateway
) {

    fun set(visible: Boolean) {
        preferences.setSkipToNextVisibility(visible)
    }

    fun observe(): Flowable<Boolean> = preferences.observeSkipToNextVisibility()

}