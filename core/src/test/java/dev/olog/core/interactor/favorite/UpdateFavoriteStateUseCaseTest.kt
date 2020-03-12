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

    private val gateway = mock<FavoriteGateway>()
    private val sut = UpdateFavoriteStateUseCase(gateway)

    @Test
    fun testInvoke() = runBlockingTest {
        val entity = FavoriteEntity(1L, FavoriteState.FAVORITE, FavoriteTrackType.TRACK)

        // when
        sut(entity)

        // then
        verify(gateway).updateFavoriteState(entity)
    }

}