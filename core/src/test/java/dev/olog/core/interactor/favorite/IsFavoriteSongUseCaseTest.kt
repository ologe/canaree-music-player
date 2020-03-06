package dev.olog.core.interactor.favorite

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class IsFavoriteSongUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        // given
        val gateway = mock<FavoriteGateway>()
        val sut = IsFavoriteSongUseCase(gateway)
        val id = 1L
        val type = FavoriteTrackType.TRACK

        // when
        sut(id, type)

        // then
        verify(gateway).isFavorite(id, type)
    }

}