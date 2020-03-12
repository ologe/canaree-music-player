package dev.olog.data

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.data.test.asSchedulers
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import org.junit.Rule
import org.junit.Test

class DataObserverTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvoke() = coroutineRule.runBlockingTest {
        coroutineRule.testDispatcher.asSchedulers()
        val callback = mock<() -> Unit>()
        val sut = DataObserver(coroutineRule.testDispatcher, callback)

        sut.onChange(false)

        verify(callback).invoke()
    }

}