package dev.olog.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.olog.data.db.AppDatabase
import org.junit.After

abstract class DatabaseTest {

    protected val db = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        AppDatabase::class.java,
    ).build()

    @After
    fun teardown() {
        db.close()
    }

}