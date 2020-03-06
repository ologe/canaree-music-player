package dev.olog.core.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.gateway.RecentSearchesGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ClearRecentSearchesUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val gateway = mock<RecentSearchesGateway>()
        val sut = ClearRecentSearchesUseCase(gateway)

        sut()

        verify(gateway).deleteAll()
    }

}