package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.Flowable
import javax.inject.Inject

class ToggleSkipToPreviousVisibilityUseCase @Inject constructor(
        private val preferences: MusicPreferencesGateway
) {

    fun set(visible: Boolean) {
        preferences.setSkipToPreviousVisibility(visible)
    }

    fun observe(): Flowable<Boolean> = preferences.observeSkipToPreviousVisibility()

}