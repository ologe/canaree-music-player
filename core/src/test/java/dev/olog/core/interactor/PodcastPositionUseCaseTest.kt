package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.gateway.podcast.PodcastGateway
import org.junit.Test

class PodcastPositionUseCaseTest {

    private val gateway = mock<PodcastGateway>()
    private val sut = PodcastPositionUseCase(gateway)

    @Test
    fun testGet() {
        val id = 1L
        val duration = 100L

        sut.get(id, duration)

        verify(gateway).getCurrentPosition(id, duration)
    }

    @Test
    fun testSet() {
        val id = 1L
        val position = 100L

        sut.set(id, position)

        verify(gateway).saveCurrentPosition(id, position)
    }

}