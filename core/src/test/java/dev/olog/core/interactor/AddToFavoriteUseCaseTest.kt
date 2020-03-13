package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class AddToFavoriteUseCaseTest {

    private val gateway = mock<FavoriteGateway>()
    private val songListUseCase = mock<GetSongListByParamUseCase>()
    private val sut = AddToFavoriteUseCase(gateway, songListUseCase)

    @Test
    fun testInvokeSingle() = runBlockingTest {
        // given
        val id = 123L
        val song = Mocks.song.copy(id = id)
        val mediaId = song.mediaId
        val type = FavoriteTrackType.TRACK
        val input = AddToFavoriteUseCase.Input(mediaId, type)

        // when
        sut(input)

        // then
        verify(gateway).addSingle(type, id)
        verifyNoMoreInteractions(gateway)
        verifyZeroInteractions(songListUseCase)
    }

    @Test
    fun testInvokeGroup() = runBlockingTest {
        // given
        val mediaId = MediaId.Category(MediaIdCategory.ALBUMS, 1)
        val type = FavoriteTrackType.TRACK
        val input = AddToFavoriteUseCase.Input(mediaId, type)
        whenever(songListUseCase.invoke(mediaId))
            .thenReturn(listOf(Mocks.song))

        // when
        sut(input)

        // then
        verify(songListUseCase).invoke(mediaId)
        verify(gateway).addGroup(type, listOf(Mocks.song.id))
        verifyNoMoreInteractions(gateway)
        verifyNoMoreInteractions(songListUseCase)
    }

}