package dev.olog.data.blacklist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.data.BlacklistQueries
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.mockTransacter
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File

class BlacklistRepositoryTest {

    private val queries = mock<BlacklistQueries>()
    private val sut = BlacklistRepository(
        queries = queries,
        schedulers = TestSchedulers(),
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test observeBlacklist`() = runTest {
        val query = QueryList("abc", "def")
        whenever(queries.selectAll()).thenReturn(query)

        sut.observeBlacklist().test(this) {
            assertValue(listOf(File("abc"), File("def")))
        }
    }

    @Test
    fun `test setBlacklist`() = runTest {
        sut.setBlacklist(listOf(File("abc"), File("def")))

        // verify is run in a transaction
        verify(queries).transaction(any(), any())
        // verify previous is deleted
        verify(queries).deleteAll()
        // verify insert
        verify(queries).insert("abc")
        verify(queries).insert("def")
    }


}