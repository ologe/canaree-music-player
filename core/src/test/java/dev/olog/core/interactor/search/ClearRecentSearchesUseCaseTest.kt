package dev.olog.core.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Rule
import org.junit.Test

class ClearRecentSearchesUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvoke() = coroutineRule.runBlocking {
        val gateway = mock<RecentSearchesGateway>()
        val sut = ClearRecentSearchesUseCase(gateway)

        sut()

        verify(gateway).deleteAll()
    }

}