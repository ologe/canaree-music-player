package dev.olog.core.interactor.favorite

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Rule
import org.junit.Test

class IsFavoriteSongUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvoke() = coroutineRule.runBlocking {
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