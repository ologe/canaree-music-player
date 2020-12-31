package dev.olog.shared.android

import org.junit.Assert
import org.junit.Test
import kotlin.time.*

class TextUtilsTest {

    @Test
    fun `formatTimeMillis, negative, should show 0`(){
        val time = (-100).milliseconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("0:00", formatted)
        Assert.assertEquals("00:00", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, milliseconds, should show 0`(){
        val time = 500.milliseconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("0:00", formatted)
        Assert.assertEquals("00:00", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, single digit seconds`(){
        val time = 8.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("0:08", formatted)
        Assert.assertEquals("00:08", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, double digit seconds`(){
        val time = 56.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("0:56", formatted)
        Assert.assertEquals("00:56", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, single digit minutes`(){
        val time = 2.minutes + 30.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("2:30", formatted)
        Assert.assertEquals("02:30", formattedWithZeros)
    }


    @Test
    fun `formatTimeMillis, double digit minutes`(){
        val time = 10.minutes + 30.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("10:30", formatted)
        Assert.assertEquals("10:30", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, single digit hours`(){
        val time = 1.hours + 25.minutes + 30.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("1:25:30", formatted)
        Assert.assertEquals("1:25:30", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, double digits hours`(){
        val time = 23.hours + 25.minutes + 30.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("23:25:30", formatted)
        Assert.assertEquals("23:25:30", formattedWithZeros)
    }

    @Test
    fun `formatTimeMillis, triple digits hours`(){
        val time = 123.hours + 25.minutes + 30.seconds

        val formatted = TextUtils.formatTimeMillis(time)
        val formattedWithZeros = TextUtils.formatTimeMillis(time, true)

        Assert.assertEquals("123:25:30", formatted)
        Assert.assertEquals("123:25:30", formattedWithZeros)
    }

}