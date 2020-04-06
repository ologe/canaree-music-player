package dev.olog.offlinelyrics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.domain.entity.OfflineLyrics
import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.offlinelyrics.BaseOfflineLyricsPresenter.Companion.ELLIPSES
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import dev.olog.test.shared.schedulers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS

class BaseOfflineLyricsPresenterTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val lyricsGateway = mock<OfflineLyricsGateway>()
    private val observeUseCase = mock<ObserveOfflineLyricsUseCase>()
    private val insertUseCase = mock<InsertOfflineLyricsUseCase>()
    private val sut = object : BaseOfflineLyricsPresenter(
        lyricsGateway, observeUseCase, insertUseCase, coroutineRule.schedulers
    ) {}

    @After
    fun teardown() {
        sut.dispose()
    }

    @Test
    fun `test simple lyrics`() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = "lalala"

        whenever(observeUseCase.invoke(id)).thenReturn(flowOf(lyrics))
        whenever(lyricsGateway.observeSyncAdjustment(any())).thenReturn(emptyFlow())

        sut.onStart()

        sut.updateCurrentTrackId(id)

        it.advanceUntilIdle()

        val actual = sut.observeLyrics().first()
        val originalLyricsActual = sut.getLyrics()

        val expected = Lyrics(listOf(OfflineLyricsLine(lyrics, 0)))
        assertEquals(expected, actual)
        assertEquals(lyrics, originalLyricsActual)
    }

    @Test
    fun `test no lyrics`() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = ""

        whenever(observeUseCase.invoke(id)).thenReturn(flowOf(lyrics))
        whenever(lyricsGateway.observeSyncAdjustment(any())).thenReturn(emptyFlow())

        sut.onStart()

        sut.updateCurrentTrackId(id)

        it.advanceUntilIdle()

        val actual = sut.observeLyrics().first()
        val originalLyricsActual = sut.getLyrics()

        val expected = Lyrics(emptyList())
        assertEquals(expected, actual)
        assertEquals(lyrics, originalLyricsActual)
    }

    @Test
    fun `test synced lyrics`() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = """
            [ti:Numb (Explicit)]
            [ar:21 Savage]
            
            [00:00.02]Grind hard ni**a grind hard I done grind hard
            [00:00.22]Late nights playin' b ball with a decoy
            [00:00:25]
            [00:00:30]
            [00:02.22]I was on the corner with the robbers and the d boys
            [00:22.22]  Now it's Louis rags when my nose runnin'
            
            [02:22.22]Now I'm on the charts man this sh*t is unbelievable
            
            [22:22.22]Mama it's a lot of secrets that I had to keep from you
            
            [40]test
            [40:00]test
            [40:00:0]test
        """.trimIndent()

        whenever(observeUseCase.invoke(id)).thenReturn(flowOf(lyrics))
        whenever(lyricsGateway.observeSyncAdjustment(any())).thenReturn(emptyFlow())

        sut.onStart()

        sut.updateCurrentTrackId(id)

        it.advanceUntilIdle()

        val actual = sut.observeLyrics().first()
        val originalLyricsActual = sut.getLyrics()

        val expected = Lyrics(listOf(
            OfflineLyricsLine(
                "Grind hard ni**a grind hard I done grind hard",
                20
            ), OfflineLyricsLine(
                "Late nights playin' b ball with a decoy",
                220
            ), OfflineLyricsLine(
                ELLIPSES,
                250
            ), OfflineLyricsLine(
                "I was on the corner with the robbers and the d boys",
                SECONDS.toMillis(2) + 220
            ), OfflineLyricsLine(
                "Now it's Louis rags when my nose runnin'",
                SECONDS.toMillis(22) + 220
            ), OfflineLyricsLine(
                "Now I'm on the charts man this sh*t is unbelievable",
                MINUTES.toMillis(2) + SECONDS.toMillis(22) + 220
            ), OfflineLyricsLine(
                "Mama it's a lot of secrets that I had to keep from you",
                MINUTES.toMillis(22) + SECONDS.toMillis(22) + 220
            )
        ))
        assertEquals(expected, actual)
        assertEquals(lyrics, originalLyricsActual)
    }

    @Test
    fun `test update sync adjustement`() = coroutineRule.runBlockingTest {
        val id = 123L
        val adjustment = 2000L

        whenever(observeUseCase.invoke(id)).thenReturn(emptyFlow())
        whenever(lyricsGateway.observeSyncAdjustment(any())).thenReturn(emptyFlow())

        sut.onStart()
        sut.updateCurrentTrackId(id)

        it.advanceUntilIdle()

        sut.updateSyncAdjustment(adjustment)

        verify(lyricsGateway).setSyncAdjustment(id, adjustment)
    }

    @Test
    fun `test get sync adjustement`() = coroutineRule.runBlockingTest {
        val id = 123L
        val adjustment = 2000L

        whenever(observeUseCase.invoke(id)).thenReturn(emptyFlow())
        whenever(lyricsGateway.observeSyncAdjustment(any())).thenReturn(emptyFlow())
        whenever(lyricsGateway.getSyncAdjustment(id)).thenReturn(adjustment)

        sut.onStart()
        sut.updateCurrentTrackId(id)

        it.advanceUntilIdle()

        val actual = sut.getSyncAdjustment()

        assertEquals("$adjustment", actual)
    }

    @Test
    fun `test update lyrics`() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = "lalala"

        whenever(observeUseCase.invoke(id)).thenReturn(emptyFlow())
        whenever(lyricsGateway.observeSyncAdjustment(any())).thenReturn(emptyFlow())

        sut.onStart()
        sut.updateCurrentTrackId(id)

        it.advanceUntilIdle()

        sut.updateLyrics(lyrics)

        verify(insertUseCase).invoke(OfflineLyrics(id, lyrics))
    }

}