package dev.olog.lib.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test

class ContentResolverExtensionsIntegrationTest {

    private lateinit var cursor: Cursor
    private lateinit var contentResolver: ContentResolver

    @Before
    fun setup() {
        cursor = MatrixCursor(arrayOf("id", "name")).apply {
            addRow(arrayOf(1L, "title 1"))
            addRow(arrayOf(2L, "title 2"))
        }
        contentResolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
    }

    @After
    fun teardown() {
        if (!cursor.isClosed) {
            cursor.close()
        }
    }

    @Test
    fun testQueryAll() {
        val result = contentResolver.queryAll(cursor) {
            it.getLong("id") to it.getString("name")
        }

        assertEquals(
            listOf(
                1L to "title 1",
                2L to "title 2"
            ),
            result
        )
    }

    @Test
    fun testQueryOne() {
        val result = contentResolver.queryOne(cursor) {
            it.getLong("id") to it.getString("name")
        }

        assertEquals(
            1L to "title 1",
            result
        )
    }

    @Test
    fun testQueryOneFail() {
        val cursor = MatrixCursor(arrayOf("id", "name"))

        val result = contentResolver.queryOne(cursor) {
            it.getLong("id") to it.getString("name")
        }

        assertNull(result)
    }

    @Test
    fun testQueryCountRows() {
        val result = contentResolver.queryCountRow(cursor)

        assertEquals(
            2,
            result
        )
    }

}