package dev.olog.data.db

import android.content.Context
import androidx.core.content.edit
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.data.blacklist.BlacklistPreferenceLegacy
import dev.olog.data.db.migration.Migration15to16
import dev.olog.data.db.migration.Migration16to17
import dev.olog.data.db.migration.Migration17to18
import dev.olog.data.db.migration.Migration18to19
import dev.olog.data.prefs.sort.AppSortingImpl
import dev.olog.data.prefs.sort.DetailSortingHelper
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MigrationTest {

    companion object {
        private const val DATABASE_NAME = "migration-db"
    }

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )
    private val sharedPrefs = InstrumentationRegistry.getInstrumentation().context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val blacklistPreferenceLegacy = BlacklistPreferenceLegacy(sharedPrefs)
    private val sortPreferences = AppSortingImpl(sharedPrefs, DetailSortingHelper(sharedPrefs))

    @Before
    fun setup() {
        sharedPrefs.edit {
            putStringSet(BlacklistPreferenceLegacy.BLACKLIST_KEY, setOf("abc", "def"))
        }
    }

    @After
    fun teardown() {
        sharedPrefs.edit { clear() }
    }

    @Test
    fun migrate15to16() {
        testMigration(Migration15to16())
    }

    @Test
    fun migrate16to17() {
        testMigration(Migration16to17())
    }

    @Test
    fun migrate17to18() {
        testMigration(Migration17to18())
    }

    @Test
    fun migrate18to19() {
        val migration = Migration18to19(
            blacklistPreferenceLegacy = blacklistPreferenceLegacy,
            sortPreferences = sortPreferences,
        )
        helper.createDatabase(DATABASE_NAME, migration.startVersion).also { it.close() }
        val db = helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration)

        db.query("SELECT COUNT(*) FROM blacklist").use {
            it.moveToFirst()
            Assert.assertEquals(2, it.getInt(0))
        }
    }

    private fun testMigration(migration: Migration) {
        helper.createDatabase(DATABASE_NAME, migration.startVersion).also { it.close() }
        helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration).also { it.close() }
    }

}