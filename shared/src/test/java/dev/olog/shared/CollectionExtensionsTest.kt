package dev.olog.shared

import org.junit.Assert
import org.junit.Test

class CollectionExtensionsTest {

    @Test
    fun testGroupByRows4Items() {
        val list = listOf(0, 1, 2, 3)

        val expected = listOf(
            listOf(0, 1, 2),
            listOf(3)
        )


        Assert.assertEquals(expected, list.groupByRows(3))
    }

    @Test
    fun testGroupByRows5Items() {
        val list = listOf(0, 1, 2, 3, 4)

        val expected = listOf(
            listOf(0, 1, 2),
            listOf(3, 4)
        )


        Assert.assertEquals(expected, list.groupByRows(3))
    }

    @Test
    fun testGroupByRows6Items() {
        val list = listOf(0, 1, 2, 3, 4, 5)

        val expected = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5)
        )


        Assert.assertEquals(expected, list.groupByRows(3))
    }


}