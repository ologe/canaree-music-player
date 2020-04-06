package dev.olog.domain.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.GENRES
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.UpdatePlayingQueueUseCase.Request
import org.junit.Test

class UpdatePlayingQueueUseCaseTest {

    private val gateway = mock<PlayingQueueGateway>()
    private val sut = UpdatePlayingQueueUseCase(gateway)

    @Test
    fun testInvoke(){
        // given
        val mediaId = Category(GENRES, 1)
        val sondId = 1L
        val idInPlaylistId = 10

        val param = Request(mediaId, sondId, idInPlaylistId)

        sut(listOf(param))

        verify(gateway).update(listOf(param))
    }

}