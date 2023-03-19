package dev.olog.data.db

import android.content.SharedPreferences
import android.os.Environment
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

class SharedPreferenceMigration @Inject constructor(
    private val prefs: SharedPreferences,
) {

    companion object {
        private const val BLACKLIST = "AppPreferencesDataStoreImpl.BLACKLIST"
    }

    @WorkerThread
    fun migrate(db: SupportSQLiteDatabase) = runBlocking {
        if (!prefs.contains(BLACKLIST)) {
            // already migrated
            return@runBlocking
        }

        // ensure Environment.getExternalStorageDirectory has a valid path, otherwise ignore migration
        val externalStorage = Environment.getExternalStorageDirectory()
            ?.takeUnless { it.path.isNullOrBlank() }?.path
            ?: return@runBlocking

        val data = prefs.getStringSet(BLACKLIST, setOf())!!
        val relativePaths = data.map { convertFullPathToMediaStoreRelativePath(externalStorage, it) }
        val blacklistValues = relativePaths.joinToString(
            separator = ",",
            postfix = ";",
            transform = { "('$it')" }
        )

        db.execSQL("INSERT INTO blacklist(directory) VALUES $blacklistValues")

        prefs.edit(commit = true) { remove(BLACKLIST) }
    }

    // /storage/emulated/0/1VRecorder           -> 1VRecorder/
    // /storage/emulated/0/Notifications/Calm   -> Notifications/Calm/
    // /storage/emulated/0/Music/hip hop        -> Music/hip hop/
    // /storage/emulated/0/Music                -> Music/
    private fun convertFullPathToMediaStoreRelativePath(
        externalStorageDirectory: String,
        path: String,
    ): String {
        return path
            .removePrefix(externalStorageDirectory)
            .removePrefix(File.separator) + File.separator
    }

}