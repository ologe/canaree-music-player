package dev.olog.domain.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.domain.gateway.RecentSearchesGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ClearRecentSearchesUseCaseTest {

    private val gateway = mock<RecentSearchesGateway>()
    private val sut = ClearRecentSearchesUseCase(gateway)

    @Test
    fun testInvoke() = runBlockingTest {
        sut()

        verify(gateway).deleteAll()
    }

}