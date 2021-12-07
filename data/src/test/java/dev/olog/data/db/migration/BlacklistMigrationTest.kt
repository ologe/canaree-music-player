package dev.olog.data.db.migration

import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nhaarman.mockitokotlin2.*
import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import dev.olog.data.extension.ContentValues
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class BlacklistMigrationTest {

    private val mockPreference = mock<Preference<Set<String>>>()
    private val db = mock<SupportSQLiteDatabase>()
    private val preferenceManager = mock<PreferenceManager>()
    private val sut = BlacklistMigration(preferenceManager)

    @Test
    fun `not migrate when preference is empty`() {
        whenever(mockPreference.get()).thenReturn(emptySet())
        whenever(preferenceManager.create(eq("AppPreferencesDataStoreImpl.BLACKLIST"), any<Set<String>>()))
            .thenReturn(mockPreference)

        // when
        sut.migrate(db)

        verifyZeroInteractions(db)
    }

    @Test
    fun `not migrate when preference is empty string`() {
        whenever(mockPreference.get()).thenReturn(setOf(""))
        whenever(preferenceManager.create(eq("AppPreferencesDataStoreImpl.BLACKLIST"), any<Set<String>>()))
            .thenReturn(mockPreference)

        // when
        sut.migrate(db)

        verifyZeroInteractions(db)
    }

    @Test
    fun `migrate when preference have data`() {
        whenever(mockPreference.get()).thenReturn(setOf("/path"))
        whenever(preferenceManager.create(eq("AppPreferencesDataStoreImpl.BLACKLIST"), any<Set<String>>()))
            .thenReturn(mockPreference)

        // when
        sut.migrate(db)

        verify(db).insert(
            "blacklist",
            SQLiteDatabase.CONFLICT_IGNORE,
            ContentValues("path" to "/path")
        )
    }

}