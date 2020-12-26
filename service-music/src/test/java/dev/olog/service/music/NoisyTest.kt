package dev.olog.service.music

import android.app.Service
import com.nhaarman.mockitokotlin2.*
import org.junit.Test

class NoisyTest {

    private val service = mock<Service>()
    private val eventDispatcher = mock<EventDispatcher>()

    private val noisy = Noisy(service, eventDispatcher)

    @Test
    fun `test lifecycle unsubscribe`() {
        val spy = spy(noisy)

        spy.onDestroy(mock())
        verify(spy).unregister()
    }

    @Test
    fun `test register`() {
        noisy.register()

        verify(service).registerReceiver(any(), any())
    }

    @Test
    fun `test multiple register`() {
        noisy.register()
        noisy.register()
        noisy.register()

        verify(service).registerReceiver(any(), any())
    }

    @Test
    fun `test unregister without previous registration`() {
        noisy.unregister()

        verifyZeroInteractions(service)
    }

    @Test
    fun `test unregister success`() {
        noisy.register()
        noisy.unregister()

        verify(service).unregisterReceiver(any())
    }

    @Test
    fun `test multiple unregister`() {
        noisy.register()

        noisy.unregister()
        noisy.unregister()
        noisy.unregister()

        verify(service).unregisterReceiver(any())
    }

}