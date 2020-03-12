package dev.olog.core.interactor.favorite

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.gateway.FavoriteGateway
import org.junit.Test

class ObserveFavoriteAnimationUseCaseTest {

    private val gateway = mock<FavoriteGateway>()
    private val sut = ObserveFavoriteAnimationUseCase(gateway)

    @Test
    fun testInvoke() {
        // when
        sut()

        // then
        verify(gateway).observeToggleFavorite()
    }

}