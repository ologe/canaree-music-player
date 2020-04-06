package dev.olog.domain.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.EqualizerPreferencesGateway
import dev.olog.domain.prefs.MusicPreferencesGateway
import org.junit.Test

class ResetPreferencesUseCaseTest {

    private val appPrefs = mock<AppPreferencesGateway>()
    private val musicPrefs = mock<MusicPreferencesGateway>()
    private val equalizerPrefs = mock<EqualizerPreferencesGateway>()
    private val blacklistPrefs = mock<BlacklistPreferences>()

    private val sut = ResetPreferencesUseCase(
        appPrefs, musicPrefs, equalizerPrefs, blacklistPrefs
    )

    @Test
    fun testInvoke() {
        sut()

        verify(appPrefs).setDefault()
        verify(musicPrefs).setDefault()
        verify(equalizerPrefs).setDefault()
        verify(blacklistPrefs).setDefault()
    }

}