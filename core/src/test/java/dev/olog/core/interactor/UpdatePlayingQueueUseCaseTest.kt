package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.GENRES
import dev.olog.core.gateway.PlayingQueueGateway
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

        val param = UpdatePlayingQueueUseCase.Request(mediaId, sondId, idInPlaylistId)

        sut(listOf(param))

        verify(gateway).update(listOf(param))
    }

}