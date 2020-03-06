package dev.olog.core.interactor.favorite

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.gateway.FavoriteGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ObserveFavoriteAnimationUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        // given
        val gateway = mock<FavoriteGateway>()
        val sut = ObserveFavoriteAnimationUseCase(gateway)

        // when
        sut()

        // then
        verify(gateway).observeToggleFavorite()
    }

}