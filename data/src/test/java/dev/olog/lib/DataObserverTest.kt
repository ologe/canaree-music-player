package dev.olog.lib

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import org.junit.Rule
import org.junit.Test

class DataObserverTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvoke() = coroutineRule.runBlockingTest {
        val callback = mock<() -> Unit>()
        val sut = DataObserver(coroutineRule.testDispatcher, callback)

        sut.onChange(false)

        verify(callback).invoke()
    }

}