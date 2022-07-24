package dev.olog.data.db

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.data.db.migration.Migration15to16
import dev.olog.data.db.migration.Migration16to17
import dev.olog.data.db.migration.Migration17to18
import dev.olog.data.db.migration.Migration18to19
import org.junit.Rule
import org.junit.Test

// todo move to unit test
class MigrationTest {

    companion object {
        private const val DATABASE_NAME = "migration-db"
    }

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

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
        testMigration(Migration18to19())
    }

    private fun testMigration(migration: Migration) {
        helper.createDatabase(DATABASE_NAME, migration.startVersion).also { it.close() }
        helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration)
    }

}