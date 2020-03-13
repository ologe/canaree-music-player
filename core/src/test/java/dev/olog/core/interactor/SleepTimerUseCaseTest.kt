package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.gateway.AlarmService
import dev.olog.core.prefs.AppPreferencesGateway
import junit.framework.Assert.assertEquals
import org.junit.Test

class SleepTimerUseCaseTest {

    private val gateway = mock<AppPreferencesGateway>()
    private val alarmService = mock<AlarmService>()
    private val sut = SleepTimerUseCase(gateway, alarmService)

    @Test
    fun `test get last`() {
        val from = 50L
        val time = 100L
        whenever(gateway.getSleepFrom()).thenReturn(from)
        whenever(gateway.getSleepTime()).thenReturn(time)

        val actual = sut.getLast()

        assertEquals(from, actual.fromWhen)
        assertEquals(time, actual.sleepTime)
    }

    @Test
    fun `test set`() {
        val from = 50L
        val time = 100L
        val until = from + time
        sut.set(from, time, until)

        verify(gateway).setSleepTimer(from, time)
        verify(alarmService).set(until)
    }

    @Test
    fun `test reset`() {
        sut.reset()

        verify(gateway).resetSleepTimer()
        verify(alarmService).resetTimer()
    }
}