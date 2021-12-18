package dev.olog.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.olog.data.dagger.RecentSearchesAdapter
import dev.olog.data.dagger.SortAdapter

object DatabaseFactory {

    private const val DB_NAME = "canaree.db"

    fun createAndroid(
        context: Context,
        inMemory: Boolean = false
    ): Database {
        val schema = Database.Schema
        val driver = AndroidSqliteDriver(
            schema = schema,
            context = context,
            name = if (inMemory) null else DB_NAME,
            callback = Callback(context, schema)
        )
        return create(driver)
    }

    fun create(driver: SqlDriver): Database {
        return Database(
            driver = driver,
            sortAdapter = SortAdapter,
            recent_searchesAdapter = RecentSearchesAdapter,
        )
    }

    private class Callback(
        private val context: Context,
        schema: SqlDriver.Schema
    ) : AndroidSqliteDriver.Callback(schema) {

        override fun onConfigure(db: SupportSQLiteDatabase) {
            for (pragma in pragmas) {
                db.query(pragma).use { it.moveToFirst() }
            }
        }

    }

    val pragmas = listOf(
        "PRAGMA journal_mode = WAL;",
        "PRAGMA temp_store = MEMORY;",
        // set synchronous to normal(1), should be safe with WAL
        // see https://www.sqlite.org/pragma.html#pragma_synchronous
        "PRAGMA synchronous = 1;",
        "PRAGMA foreign_keys = 1;",
    )

}