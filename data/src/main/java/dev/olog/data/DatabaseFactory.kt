package dev.olog.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.olog.data.Database
import dev.olog.data.dagger.RecentSearchesAdapter
import dev.olog.data.dagger.SortAdapter
import dev.olog.data.extension.ContentValues

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

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            updateAutoPlaylist(db, -1, context.getString(localization.R.string.common_last_added))
            updateAutoPlaylist(db, -2, context.getString(localization.R.string.common_favorites))
            updateAutoPlaylist(db, -3, context.getString(localization.R.string.common_history))
            updateAutoPlaylist(db, -4, context.getString(localization.R.string.common_last_added))
            updateAutoPlaylist(db, -5, context.getString(localization.R.string.common_favorites))
            updateAutoPlaylist(db, -6, context.getString(localization.R.string.common_history))
        }

        private fun updateAutoPlaylist(
            db: SupportSQLiteDatabase,
            id: Long,
            name: String,
        ) {
            db.update(
                "indexed_playlists",
                SQLiteDatabase.CONFLICT_FAIL,
                ContentValues("title" to name),
                "id = ?",
                arrayOf(id)
            )
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