package dev.olog.data.db

import android.content.Context
import android.database.Cursor
import androidx.core.content.edit
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.data.blacklist.BlacklistPreferenceLegacy
import dev.olog.data.db.migration.Migration15to16
import dev.olog.data.db.migration.Migration16to17
import dev.olog.data.db.migration.Migration17to18
import dev.olog.data.db.migration.Migration18to19
import dev.olog.data.song.folder.FolderMostPlayedEntity
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
    fun testAllMigrations() {
        val migrations = listOf(
            // previously schema are lost
            Migration15to16(),
            Migration16to17(),
            Migration17to18(),
            Migration18to19(),
        )

        helper.createDatabase(DATABASE_NAME, 15).apply { close() }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            DATABASE_NAME,
        ).addMigrations(*migrations.toTypedArray()).build().apply {
            openHelper.writableDatabase.close()
        }
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
        val migration = Migration18to19()
        helper.createDatabase(DATABASE_NAME, migration.startVersion).also {
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(1, 'dir1');")
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(1, 'dir1');")
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(1, 'dir1');")
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(1, 'dir2');")
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(1, 'dir2');")
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(2, 'dir2');")
            it.execSQL("INSERT INTO most_played_folder(songId, folderPath) VALUES(2, 'dir2');")
            it.close()
        }
        val db = helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration)

        db.query("SELECT songId, path, timesPlayed FROM most_played_folder_v2").use {
            Assert.assertEquals(3, it.count)
            it.moveToNext()
            Assert.assertEquals(
                FolderMostPlayedEntity("1", "dir1", 3),
                it.toFolderMostPlayedEntity()
            )
            it.moveToNext()
            Assert.assertEquals(
                FolderMostPlayedEntity("1", "dir2", 2),
                it.toFolderMostPlayedEntity()
            )
            it.moveToNext()
            Assert.assertEquals(
                FolderMostPlayedEntity("2", "dir2", 2),
                it.toFolderMostPlayedEntity()
            )
        }

        db.close()
    }

    private fun Cursor.toFolderMostPlayedEntity() = FolderMostPlayedEntity(
        songId = getString(0),
        path = getString(1),
        timesPlayed = getInt(2),
    )

    private fun testMigration(migration: Migration) {
        helper.createDatabase(DATABASE_NAME, migration.startVersion).also { it.close() }
        helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration).also { it.close() }
    }

}