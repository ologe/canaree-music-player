package dev.olog.offlinelyrics.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.Mocks
import dev.olog.test.shared.runBlockingTest
import dev.olog.test.shared.schedulers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ObserveOfflineLyricsUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val gateway = mock<OfflineLyricsGateway>()
    private val trackGateway = mock<TrackGateway>()
    private val readPersistedLyrics = mock<ReadPersistedLyricsUseCase>()
    private val sut = ObserveOfflineLyricsUseCase(
        gateway, trackGateway, coroutineRule.schedulers, readPersistedLyrics
    )

    @Test
    fun `test get non persisted lyrics`() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = "lalala"
        val song = Mocks.song.copy(id = id)

        whenever(trackGateway.getByParam(id)).thenReturn(song)
        whenever(readPersistedLyrics.invoke(song)).thenThrow(RuntimeException::class.java)
        whenever(gateway.observeLyrics(id)).thenReturn(flowOf(lyrics))

        // when
        val actual = sut.invoke(id).first()

        assertEquals(
            lyrics,
            actual
        )
    }

    @Test
    fun `test get persisted lyrics`() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = "lalala"
        val persistedLyrics = "persistend lalala"
        val song = Mocks.song.copy(id = id)

        whenever(trackGateway.getByParam(id)).thenReturn(song)
        whenever(gateway.observeLyrics(id)).thenReturn(flowOf(lyrics))
        whenever(readPersistedLyrics.invoke(song)).thenReturn(persistedLyrics)

        // when
        val actual = sut.invoke(id).first()

        assertEquals(
            persistedLyrics,
            actual
        )
    }

}