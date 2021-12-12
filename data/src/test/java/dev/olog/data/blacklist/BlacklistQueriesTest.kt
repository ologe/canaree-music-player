package dev.olog.data.blacklist

import dev.olog.data.Blacklist
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class BlacklistQueriesTest {

    private val db = TestDatabase()
    private val blacklistQueries = db.blacklistQueries

    @Test
    fun test() {
        // initial values is empty
        Assert.assertEquals(emptyList<Blacklist>(), blacklistQueries.selectAll().executeAsList())

        // insert 2 values
        blacklistQueries.insert("path1")
        blacklistQueries.insert("path2")

        // have to contains these 2 paths
        Assert.assertEquals(
            listOf("path1", "path2"),
            blacklistQueries.selectAll().executeAsList()
        )

        // clear all
        blacklistQueries.deleteAll()
        Assert.assertEquals(emptyList<Blacklist>(), blacklistQueries.selectAll().executeAsList())
    }

}