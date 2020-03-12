package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.Mocks
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AddToFavoriteUseCaseTest {

    private val gateway = mock<FavoriteGateway>()
    private val songListUseCase = mock<GetSongListByParamUseCase>()
    private val sut = AddToFavoriteUseCase(gateway, songListUseCase)

    @Test
    fun testInvokeSingle() = runBlocking {
        // given
        val song = Mocks.song
        val mediaId = song.mediaId
        val type = FavoriteTrackType.TRACK
        val input = AddToFavoriteUseCase.Input(mediaId, type)

        // when
        sut(input)

        // then
        verify(gateway).addSingle(type, song.id)
        verifyNoMoreInteractions(gateway)
        verifyZeroInteractions(songListUseCase)
    }

    @Test
    fun testInvokeGroup() = runBlocking {
        // given
        val album = Mocks.album
        val mediaId = album.mediaId
        val type = FavoriteTrackType.TRACK
        val input = AddToFavoriteUseCase.Input(mediaId, type)
        whenever(songListUseCase.invoke(mediaId))
            .thenReturn(listOf(Mocks.song))

        // when
        sut(input)

        // then
        verify(songListUseCase).invoke(mediaId)
        verify(gateway).addGroup(type, listOf(Mocks.song.id))
        verifyZeroInteractions(gateway)
        verifyZeroInteractions(songListUseCase)
    }

}