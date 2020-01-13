package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.core.prefs.MusicPreferencesGateway
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