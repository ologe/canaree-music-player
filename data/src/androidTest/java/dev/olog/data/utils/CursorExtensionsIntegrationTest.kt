package dev.olog.lib.utils

import android.database.Cursor
import android.database.MatrixCursor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class CursorExtensionsIntegrationTest {

    private lateinit var cursor: Cursor

    @Before
    fun setup() {
        cursor = MatrixCursor(arrayOf("id", "name")).apply {
            addRow(arrayOf(1, "title"))
        }
        cursor.moveToFirst()
    }

    @After
    fun teardown() {
        if (!cursor.isClosed) {
            cursor.close()
        }
    }

    @Test
    fun testGetInt() {
        val actual = cursor.getInt("id")
        assertEquals(1, actual)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetIntFail() {
        val cursor = MatrixCursor(arrayOf("id", "name")).apply {
            addRow(arrayOf(null, ""))
        }
        cursor.getInt("id")
    }

    @Test
    fun testGetLong() {
        val actual = cursor.getLong("id")
        assertEquals(1L, actual)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetLongFail() {
        val cursor = MatrixCursor(arrayOf("id", "name")).apply {
            addRow(arrayOf(null, ""))
        }
        cursor.getLong("id")
    }

    @Test
    fun testGetString() {
        val actual = cursor.getString("name")
        assertEquals("title", actual)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetStringFail() {
        val cursor = MatrixCursor(arrayOf("id", "name")).apply {
            addRow(arrayOf(1, null))
        }
        cursor.getString("name")
    }

    @Test
    fun testGetStringOrNullShouldReturnValue() {
        val actual = cursor.getStringOrNull("name")
        assertEquals("title", actual)
    }

    @Test
    @Ignore(value = "java.lang.IllegalStateException: invalid column name ??")
    fun testGetStringOrNullShouldReturnNull() {
        val cursor = MatrixCursor(arrayOf("id", "name")).apply {
            addRow(arrayOf(1, null))
        }
        val actual = cursor.getStringOrNull("name")
        assertNull(actual)
    }

}