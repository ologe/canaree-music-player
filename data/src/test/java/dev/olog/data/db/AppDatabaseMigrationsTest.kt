package dev.olog.data.db

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.shared.throwNotHandled
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// unfortunately schemas starts with 15
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AppDatabaseMigrationsTest {

    companion object {
        private const val DB = "migration_test"
    }

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun `test migration 15 to 16`() {
        runMigration(15, 16)
    }

    @Test
    fun `test migration 16 to 17`() {
        runMigration(16, 17)
    }

    @Test
    fun `test migration 17 to 18`() {
        runMigration(17, 18)
    }

    @Test
    fun `test migration 18 to 19`() {
        runMigration(18, 19)
    }

    private fun runMigration(
        from: Int,
        to: Int,
        doOnPreMigration: SupportSQLiteDatabase.() -> Unit = {},
        doOnPostMigration: SupportSQLiteDatabase.() -> Unit = {}
    ) {
        helper.createDatabase(DB, from).apply {
            doOnPreMigration()
            close()
        }

        helper.runMigrationsAndValidate(
            DB,
            to,
            true,
            getMigration(from, to)
        ).apply {
            doOnPostMigration()
            close()
        }
    }

    private fun getMigration(from: Int, to: Int): Migration {
        return when {
            from == 15 && to == 16 -> AppDatabaseMigrations.Migration_15_16
            from == 16 && to == 17 -> AppDatabaseMigrations.Migration_16_17
            from == 17 && to == 18 -> AppDatabaseMigrations.Migration_17_18
            from == 18 && to == 19 -> AppDatabaseMigrations.Migration_18_19
            else -> throwNotHandled("$from to $to")
        }
    }

}