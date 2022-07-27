package dev.olog.data

import org.junit.Assert
import org.junit.Test
import kotlin.time.Duration.Companion.days

class DataConstantsTest {

    @Test
    fun test() {
        Assert.assertEquals(
            14.days.inWholeSeconds,
            DataConstants.RECENTLY_ADDED_PERIOD
        )

        Assert.assertEquals(10, DataConstants.MAX_LAST_PLAYED)
    }

}