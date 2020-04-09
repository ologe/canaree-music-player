package dev.olog.lib

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.olog.lib.db.AppDatabase
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher

internal object DatabaseBuilder {

    fun build(dispatcher: TestCoroutineDispatcher): AppDatabase {
        val context = ApplicationProvider.getApplicationContext<Application>()
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(dispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()
    }

}