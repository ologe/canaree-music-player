package dev.olog.data

import org.junit.Assert
import org.junit.Test
import kotlin.time.Duration.Companion.days

class DataConstantsTest {

    @Test
    fun test() {
        Assert.assertEquals(
            14.days.inWholeSeconds,
            DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS
        )

        Assert.assertEquals(10, DataConstants.MAX_LAST_PLAYED)

        Assert.assertEquals(5, DataConstants.MIN_MOST_PLAYED_TIMES)
        Assert.assertEquals(10, DataConstants.MAX_MOST_PLAYED_ITEMS)
    }

}