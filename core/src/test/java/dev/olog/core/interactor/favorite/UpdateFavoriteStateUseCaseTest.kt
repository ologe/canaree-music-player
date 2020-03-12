package dev.olog.core.interactor.favorite

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.entity.favorite.FavoriteEntity
import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class UpdateFavoriteStateUseCaseTest {

    @Test
    fun testInvoke() = runBlockingTest {
        // given
        val gateway = mock<FavoriteGateway>()
        val sut = UpdateFavoriteStateUseCase(gateway)
        val entity = FavoriteEntity(1L, FavoriteState.FAVORITE, FavoriteTrackType.TRACK)

        // when
        sut(entity)

        // then
        verify(gateway).updateFavoriteState(entity)
    }

}