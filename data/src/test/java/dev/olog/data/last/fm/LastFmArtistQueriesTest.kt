package dev.olog.data.last.fm

import dev.olog.core.DateTimeFactory
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class LastFmArtistQueriesTest {

    private val db = TestDatabase()
    private val queries = db.lastFmArtistQueries

    @Test
    fun `less than last month should be still valid`() {
        Assert.assertEquals(null, queries.selectById("1").executeAsOneOrNull())

        val instant = LocalDate.now()
            .minusMonths(1).plusDays(1)
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()

        val item = Last_fm_artist(
            id = "1",
            image_url = "image",
            added = DateTimeFactory().millisToFormattedDate(Date.from(instant).time),
            mbid = "mbid",
            wiki = "wiki"
        )

        // insert
        queries.insert(item)

        Assert.assertEquals(item, queries.selectById("1").executeAsOne())

        // delete
        queries.delete("1")
        Assert.assertEquals(null, queries.selectById("1").executeAsOneOrNull())
    }

    @Test
    fun `more than last month should be invalid`() {
        Assert.assertEquals(null, queries.selectById("1").executeAsOneOrNull())

        val instant = LocalDate.now()
            .minusMonths(1).minusDays(1)
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()

        val item = Last_fm_artist(
            id = "1",
            image_url = "image",
            added = DateTimeFactory().millisToFormattedDate(Date.from(instant).time),
            mbid = "mbid",
            wiki = "wiki"
        )

        // insert
        queries.insert(item)

        Assert.assertEquals(null, queries.selectById("1").executeAsOneOrNull())
    }

}