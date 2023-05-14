package dev.olog.data.db

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import dev.olog.data.db.entities.CustomTypeConverters
import dev.olog.data.db.migration.Migration15To16
import dev.olog.data.db.migration.Migration16To17
import dev.olog.data.db.migration.Migration17To18
import dev.olog.data.db.migration.Migration18To19
import dev.olog.data.mediastore.MediaStoreUris
import dev.olog.data.utils.ContentValues
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
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
        val migrations = arrayOf(
            Migration15To16(),
            Migration16To17(),
            Migration17To18(),
            Migration18To19(mockk()),
        )

        helper.createDatabase(DATABASE_NAME, 15).apply { close() }

        val db = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            DATABASE_NAME,
        ).addMigrations(*migrations)
            .addTypeConverter(CustomTypeConverters(Json))
            .build()
            .apply {
                openHelper.writableDatabase.close()
            }

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
    @Suppress("DEPRECATION")
    fun testMigration18To19() {
        val contentResolver = mockk<ContentResolver> {
            // playlist
            mockPlaylistInsertion(
                playlistIndex = 1,
            )
            mockPlaylistInsertion(
                playlistIndex = 2,

            )
            // podcast playlists
            mockPlaylistInsertion(
                playlistIndex = 3,

            )
            mockPlaylistInsertion(
                playlistIndex = 4,
            )
            every { bulkInsert(any(), any()) } returns 0
        }
        val migration = Migration18To19(contentResolver)

        helper.createDatabase(DATABASE_NAME, migration.startVersion).apply {
            // insert playlists
            //language=RoomSql
            execSQL("INSERT INTO playlist VALUES (1, 'p1', 0), (2, 'p2', 0)")

            // insert podcast playlists
            //language=RoomSql
            execSQL("INSERT INTO podcast_playlist VALUES (3, 'p3', 0), (4, 'p4', 0)")

            // insert playlist tracks
            //language=RoomSql
            execSQL("""
                INSERT INTO playlist_tracks(idInPlaylist, trackId, playlistId) VALUES 
                    (1, 100, 1),
                    (2, 101, 1),
                    (3, 102, 1),
                    (1, 110, 2),
                    (2, 111, 2);
            """)

            // insert playlist podcast podcasts
            //language=RoomSql
            execSQL("""
                INSERT INTO podcast_playlist_tracks(idInPlaylist, podcastId, playlistId) VALUES 
                    (2, 200, 3),
                    (3, 201, 3),
                    (4, 202, 3),
                    (1, 210, 4),
                    (2, 211, 4);
            """)

            close()
        }

        helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration).apply {
            close()
        }

        verifyOrder {
            // verify playlist insertion
            contentResolver.insert(
                Uri.parse("content://media/external/audio/playlists"),
                ContentValues(Playlists.NAME to "p1")
            )
            contentResolver.insert(
                Uri.parse("content://media/external/audio/playlists"),
                ContentValues(Playlists.NAME to "p2")
            )
            contentResolver.insert(
                Uri.parse("content://media/external/audio/playlists"),
                ContentValues(Playlists.NAME to "p3")
            )
            contentResolver.insert(
                Uri.parse("content://media/external/audio/playlists"),
                ContentValues(Playlists.NAME to "p4")
            )

            // verify playlist tracks insertion
            contentResolver.bulkInsert(
                Uri.parse("content://media/external/audio/playlists/1/members"),
                arrayOf(
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 1L,
                        Playlists.Members.AUDIO_ID to 100L,
                        Playlists.Members.PLAYLIST_ID to 1L,
                    ),
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 2L,
                        Playlists.Members.AUDIO_ID to 101L,
                        Playlists.Members.PLAYLIST_ID to 1L,
                    ),
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 3L,
                        Playlists.Members.AUDIO_ID to 102L,
                        Playlists.Members.PLAYLIST_ID to 1L,
                    ),
                )
            )
            contentResolver.bulkInsert(
                Uri.parse("content://media/external/audio/playlists/2/members"),
                arrayOf(
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 1L,
                        Playlists.Members.AUDIO_ID to 110L,
                        Playlists.Members.PLAYLIST_ID to 2L,
                    ),
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 2L,
                        Playlists.Members.AUDIO_ID to 111L,
                        Playlists.Members.PLAYLIST_ID to 2L,
                    ),
                )
            )
            contentResolver.bulkInsert(
                Uri.parse("content://media/external/audio/playlists/3/members"),
                arrayOf(
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 2L,
                        Playlists.Members.AUDIO_ID to 200L,
                        Playlists.Members.PLAYLIST_ID to 3L,
                    ),
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 3L,
                        Playlists.Members.AUDIO_ID to 201L,
                        Playlists.Members.PLAYLIST_ID to 3L,
                    ),
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 4L,
                        Playlists.Members.AUDIO_ID to 202L,
                        Playlists.Members.PLAYLIST_ID to 3L,
                    ),
                )
            )
            contentResolver.bulkInsert(
                Uri.parse("content://media/external/audio/playlists/4/members"),
                arrayOf(
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 1L,
                        Playlists.Members.AUDIO_ID to 210L,
                        Playlists.Members.PLAYLIST_ID to 4L,
                    ),
                    ContentValues(
                        Playlists.Members.PLAY_ORDER to 2L,
                        Playlists.Members.AUDIO_ID to 211L,
                        Playlists.Members.PLAYLIST_ID to 4L,
                    ),
                )
            )
        }

        // TODO verify content resolver called it's stuff
    }

    private fun ContentResolver.mockPlaylistInsertion(playlistIndex: Long) {
        val playlistUri = ContentUris.withAppendedId(MediaStoreUris.playlists, playlistIndex)
        every {
            insert(MediaStoreUris.playlists, ContentValues(Playlists.NAME to "p$playlistIndex"))
        } returns playlistUri
    }

    private fun testSchemaMigration(migration: Migration) {
        helper.createDatabase(DATABASE_NAME, migration.startVersion).run { close() }
        helper.runMigrationsAndValidate(DATABASE_NAME, migration.endVersion, true, migration).run { close() }
    }

}