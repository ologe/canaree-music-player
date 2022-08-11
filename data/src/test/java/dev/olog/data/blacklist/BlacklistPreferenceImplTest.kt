package dev.olog.data.blacklist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.data.blacklist.db.BlacklistDao
import dev.olog.data.blacklist.db.BlacklistEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class BlacklistPreferenceImplTest {

    private val dao = mock<BlacklistDao>()
    private val sut = BlacklistRepository(
        dao = dao,
    )

    @Test
    fun `test getBlackList`() = runTest {
        val expected = listOf(
            BlacklistEntity("dir1"),
            BlacklistEntity("dir2"),
        )
        whenever(dao.getAll()).thenReturn(expected)

        val actual = sut.getBlackList()
        Assert.assertEquals(expected.map { it.directory }, actual)
    }

    @Test
    fun `test setBlackList`() = runTest {
        val items = listOf(
            "dir1",
            "dir2",
        )

        sut.setBlackList(items)

        verify(dao).replaceAll(items.map { BlacklistEntity(it) })
    }

}