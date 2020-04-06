package dev.olog.service.music

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.state.MusicServiceMetadata
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import dev.olog.test.shared.schedulers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MusicServiceMetadataTest {

    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    private val context = mock<Context>()
    private val mediaSession = mock<MediaSessionCompat>()
    private val musicPrefs = mock<MusicPreferencesGateway>()
    private val playerLifecycle = mock<IPlayerLifecycle>()

    private val sut = MusicServiceMetadata(
        context, mediaSession, playerLifecycle, musicPrefs, coroutinesRule.schedulers
    )

    @Test
    fun `test subscription`() = coroutinesRule.runBlockingTest {
        verify(playerLifecycle).addListener(sut)
    }

    @Test
    fun `test on metadata changed`() = coroutinesRule.runBlockingTest {

    }

}