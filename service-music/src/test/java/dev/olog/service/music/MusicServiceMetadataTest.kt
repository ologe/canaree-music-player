package dev.olog.service.music

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import dev.olog.feature.media.impl.interfaces.IPlayerLifecycle
import dev.olog.feature.media.impl.model.MetadataEntity
import dev.olog.feature.media.impl.model.SkipType
import dev.olog.service.music.shared.MusicServiceData
import dev.olog.feature.media.impl.state.MusicServiceMetadata
import dev.olog.test.shared.MainCoroutineRule
import org.junit.Rule
import org.junit.Test

class MusicServiceMetadataTest {

    @get:Rule
    var coroutinesMainDispatcherRule = MainCoroutineRule()

    private val context = mock<Context>()
    private val mediaSession = mock<MediaSessionCompat>()
    private val playerLifecycle = mock<IPlayerLifecycle>()

    private val musicServiceMetadata = MusicServiceMetadata(
        context, mediaSession, playerLifecycle, mock()
    )

    @Test
    fun `test subscription`() {
        verify(playerLifecycle).addListener(musicServiceMetadata)
    }

    @Test
    fun `test onPrepared`() {
        val item = MusicServiceData.mediaEntity
        val metadataItem = MetadataEntity(item, SkipType.NONE)

        val spy = spy(musicServiceMetadata)

        spy.onPrepare(metadataItem)

        verify(spy).onMetadataChanged(metadataItem)
    }

}