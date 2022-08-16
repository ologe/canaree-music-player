package dev.olog.data.blacklist.db

import dev.olog.data.DatabaseTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class BlacklistDaoTest : DatabaseTest() {

    private val dao = db.blacklistDao()

    @Test
    fun test() = runTest {
        // initial state, empty
        Assert.assertEquals(emptyList<List<BlacklistEntity>>(), dao.getAll())

        // insert one intem
        dao.insertAll(listOf(BlacklistEntity("dir1")))

        Assert.assertEquals(listOf(BlacklistEntity("dir1")), dao.getAll())

        // insert multiple items, also with conflicts
        dao.insertAll(listOf(BlacklistEntity("dir1")))
        dao.insertAll(listOf(
            BlacklistEntity("dir1"),
            BlacklistEntity("dir2"),
            BlacklistEntity("dir3"),
        ))

        Assert.assertEquals(
            listOf(
                BlacklistEntity("dir1"),
                BlacklistEntity("dir2"),
                BlacklistEntity("dir3"),
            ),
            dao.getAll()
        )

        // replace items
        dao.replaceAll(
            listOf(
                BlacklistEntity("dir4"),
                BlacklistEntity("dir5"),
            )
        )

        Assert.assertEquals(
            listOf(
                BlacklistEntity("dir4"),
                BlacklistEntity("dir5"),
            ),
            dao.getAll()
        )

        // clear
        dao.deleteAll()

        Assert.assertEquals(emptyList<List<BlacklistEntity>>(), dao.getAll())
    }

}