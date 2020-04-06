package dev.olog.offlinelyrics.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.domain.entity.OfflineLyrics
import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.domain.gateway.track.TrackGateway
import org.junit.Rule
import org.junit.Test

class InsertOfflineLyricsUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val gateway = mock<OfflineLyricsGateway>()
    private val trackGateway = mock<TrackGateway>()
    private val persistLyrics = mock<PersistLyricsUseCase>()
    private val sut = InsertOfflineLyricsUseCase(
        gateway, trackGateway, coroutineRule.schedulers, persistLyrics
    )

    @Test
    fun test() = coroutineRule.runBlockingTest {
        val id = 123L
        val lyrics = "lalala"
        val offlineLyrics = OfflineLyrics(id, lyrics)
        val song = Mocks.song.copy(id = id)

        whenever(trackGateway.getByParam(id)).thenReturn(song)

        // when
        sut.invoke(offlineLyrics)

        // then
        verify(persistLyrics).invoke(song, lyrics)
        verify(gateway).saveLyrics(offlineLyrics)
    }

}