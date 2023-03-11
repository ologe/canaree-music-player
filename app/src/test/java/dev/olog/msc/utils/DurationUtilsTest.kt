package dev.olog.msc.utils

import dev.olog.feature.media.api.DurationUtils
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

class DurationUtilsTest {

    @Test
    fun `readable length, millis`(){
        val time = 500L

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("0:00", formatted)
        Assert.assertEquals("00:00", formattedWithZeros)
    }

    @Test
    fun `readable length, few seconds`(){
        val time = TimeUnit.SECONDS.toMillis(8)

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("0:08", formatted)
        Assert.assertEquals("00:08", formattedWithZeros)
    }

    @Test
    fun `readable length, lot of seconds`(){
        val time = TimeUnit.SECONDS.toMillis(56)

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("0:56", formatted)
        Assert.assertEquals("00:56", formattedWithZeros)
    }

    @Test
    fun `readable length, few minutes`(){
        var time = TimeUnit.MINUTES.toMillis(2)
        time += TimeUnit.SECONDS.toMillis(30)

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("2:30", formatted)
        Assert.assertEquals("02:30", formattedWithZeros)
    }


    @Test
    fun `readable length, lot of minutes`(){
        var time = TimeUnit.MINUTES.toMillis(10)
        time += TimeUnit.SECONDS.toMillis(30)

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("10:30", formatted)
        Assert.assertEquals("10:30", formattedWithZeros)
    }

    @Test
    fun `readable length, few hours`(){
        var time = TimeUnit.HOURS.toMillis(1)
        time += TimeUnit.MINUTES.toMillis(25)
        time += TimeUnit.SECONDS.toMillis(30)

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("1:25:30", formatted)
        Assert.assertEquals("1:25:30", formattedWithZeros)
    }

    @Test
    fun `readable length, lot of hours`(){
        var time = TimeUnit.HOURS.toMillis(10)
        time += TimeUnit.MINUTES.toMillis(25)
        time += TimeUnit.SECONDS.toMillis(30)

        val formatted = DurationUtils.formatMillis(time)
        val formattedWithZeros = DurationUtils.formatMillis(time, true)

        Assert.assertEquals("10:25:30", formatted)
        Assert.assertEquals("10:25:30", formattedWithZeros)
    }

}