package dev.olog.service.music

import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.feature.media.impl.OnAudioSessionIdChangeListener
import dev.olog.test.shared.MainCoroutineRule
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class OnAudioSessionIdChangeListenerTest {

    @get:Rule
    var coroutinesMainDispatcherRule = MainCoroutineRule()

    private val lifecycle = mock<Lifecycle>()
    private val equalizer = mock<IEqualizer>()
    private val virtualizer = mock<IVirtualizer>()
    private val bassBoost = mock<IBassBoost>()

    private val sessionListener = OnAudioSessionIdChangeListener(
        lifecycle, equalizer, virtualizer, bassBoost
    )

    @Test
    fun `test lifecycle subscribe`() {
        verify(lifecycle).addObserver(sessionListener)
    }

    @Test
    fun `test on audio session id changed`() = runBlocking<Unit> {
        val latch = CountDownLatch(1)
        val audioSessionId = 1

        sessionListener.onAudioSessionId(audioSessionId)

        latch.await(OnAudioSessionIdChangeListener.DELAY, TimeUnit.MILLISECONDS)

        verify(equalizer).onAudioSessionIdChanged(audioSessionId)
        verify(virtualizer).onAudioSessionIdChanged(audioSessionId)
        verify(bassBoost).onAudioSessionIdChanged(audioSessionId)
    }

    @Test
    fun `test on audio session id changed very fast`() = runBlocking<Unit> {
        val latch = CountDownLatch(1)
        val audioSessionId1 = 1
        val audioSessionId2 = 2
        val audioSessionId3 = 3

        sessionListener.onAudioSessionId(audioSessionId1)
        sessionListener.onAudioSessionId(audioSessionId2)
        sessionListener.onAudioSessionId(audioSessionId3)

        latch.await(OnAudioSessionIdChangeListener.DELAY + 50, TimeUnit.MILLISECONDS)

        verify(equalizer).onAudioSessionIdChanged(audioSessionId3)
        verify(virtualizer).onAudioSessionIdChanged(audioSessionId3)
        verify(bassBoost).onAudioSessionIdChanged(audioSessionId3)
    }

    @Test
    fun `test release`() {
        sessionListener.release()

        verify(equalizer).onDestroy()
        verify(virtualizer).onDestroy()
        verify(bassBoost).onDestroy()
    }

}