package dev.olog.core.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.gateway.RecentSearchesGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ClearRecentSearchesUseCaseTest {

    @Test
    fun testInvoke() = runBlockingTest {
        val gateway = mock<RecentSearchesGateway>()
        val sut = ClearRecentSearchesUseCase(gateway)

        sut()

        verify(gateway).deleteAll()
    }

}