package dev.olog.domain.interactor.favorite

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.domain.entity.favorite.FavoriteItemState
import dev.olog.domain.entity.favorite.FavoriteState
import dev.olog.domain.entity.favorite.FavoriteTrackType
import dev.olog.domain.gateway.FavoriteGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class UpdateFavoriteStateUseCaseTest {

    private val gateway = mock<FavoriteGateway>()
    private val sut = UpdateFavoriteStateUseCase(gateway)

    @Test
    fun testInvoke() = runBlockingTest {
        val entity = FavoriteItemState(1L, FavoriteState.FAVORITE, FavoriteTrackType.TRACK)

        // when
        sut(entity)

        // then
        verify(gateway).updateFavoriteState(entity)
    }

}