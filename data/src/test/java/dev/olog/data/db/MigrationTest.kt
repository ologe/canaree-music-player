package dev.olog.data.db

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.data.db.entities.CustomTypeConverters
import dev.olog.data.db.migration.Migration15To16
import dev.olog.data.db.migration.Migration16To17
import dev.olog.data.db.migration.Migration17To18
import dev.olog.data.db.migration.Migration18To19
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
    fun testAllMigrations() {
        val migrations = listOf(
            Migration15To16(),
            Migration16To17(),
            Migration17To18(),
            Migration18To19(),
        )

        val db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).apply {
            addMigrations(*migrations.toTypedArray())
            addTypeConverter(CustomTypeConverters(Json))
        }.build()

        db.close()
    }

    @Test
    fun testMigration15to16() {
        testSchemaMigration(Migration15To16())
    }

    @Test
    fun testMigration16to17() {
        testSchemaMigration(Migration16To17())
    }

    @Test
    fun testMigration17to18() {
        testSchemaMigration(Migration17To18())
    }

    @Test
    fun testMigration18To19() {
        testSchemaMigration(Migration18To19())
    }

    private fun testSchemaMigration(migration: Migration) {
        helper.createDatabase(DATABASE_NAME, migration.startVersion).run { close() }
        helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration).run { close() }
    }

}