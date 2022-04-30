package dev.olog.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.data.R
import dev.olog.shared.extension.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences

) : AppPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val SLEEP_TIME = "$TAG.SLEEP_TIME"
        private const val SLEEP_FROM = "$TAG.FROM_WHEN"


        private const val LAST_FM_USERNAME = "$TAG.LAST_FM_USERNAME_2"
        private const val LAST_FM_PASSWORD = "$TAG.LAST_FM_PASSWORD_2"

        private const val DEFAULT_MUSIC_FOLDER = "$TAG.DEFAULT_MUSIC_FOLDER_2"
    }

    override fun resetSleepTimer() {
        setSleepTimer(-1L, -1L)
    }

    override fun setSleepTimer(sleepFrom: Long, sleepTime: Long) {
        preferences.edit {
            putLong(SLEEP_FROM, sleepFrom)
            putLong(SLEEP_TIME, sleepTime)
        }
    }

    override fun getSleepTime(): Long {
        return preferences.getLong(SLEEP_TIME, -1)
    }

    override fun getSleepFrom(): Long {
        return preferences.getLong(SLEEP_FROM, -1)
    }

    override fun reset() {
        // LIBRARY -> folder tree
        @Suppress("DEPRECATION")
        setDefaultMusicFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
        setDefaultFolderView()

        // USER INTERFACE
        setDefaultPlayerAppearance()
        setDefaultAdaptiveColors()
        setDefaultImmersive()
        hideQuickAction()
        setDefaultIconShape()
    }

    private fun setDefaultAdaptiveColors() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
        }
    }

    private fun setDefaultImmersive() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_immersive_key), false)
        }
    }

    override fun canAutoCreateImages(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
    }

    private fun setDefaultFolderView() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
        }
    }

    private fun hideQuickAction() {
        preferences.edit {
            putString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        }
    }

    private fun setDefaultIconShape() {
        preferences.edit {
            putString(context.getString(R.string.prefs_icon_shape_key), context.getString(R.string.prefs_icon_shape_rounded))
        }
    }

    private fun setDefaultPlayerAppearance() {
        preferences.edit {
            putString(
                context.getString(R.string.prefs_appearance_key),
                context.getString(R.string.prefs_appearance_entry_value_default)
            )
        }
    }

    /*
            Must be encrypted
         */
    override fun getLastFmCredentials(): UserCredentials {
        return UserCredentials(
            preferences.getString(LAST_FM_USERNAME, "")!!,
            preferences.getString(LAST_FM_PASSWORD, "")!!
        )
    }

    /*
        Must be encrypted
     */
    override fun observeLastFmCredentials(): Flow<UserCredentials> {
        return preferences.observeKey(LAST_FM_USERNAME, "")
            .map { username ->
                UserCredentials(username, preferences.getString(LAST_FM_PASSWORD, "")!!)
            }
    }

    /*
        Must be encrypted
     */
    override fun setLastFmCredentials(user: UserCredentials) {
        preferences.edit {
            putString(LAST_FM_USERNAME, user.username)
            putString(LAST_FM_PASSWORD, user.password)
        }
    }

    @Suppress("DEPRECATION")
    private fun defaultFolder(): String {
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        var startFolder = Environment.getRootDirectory()
        if (musicDir.exists() && musicDir.isDirectory) {
            startFolder = musicDir
        } else {
            val externalStorage = Environment.getExternalStorageDirectory()
            if (externalStorage.exists() && externalStorage.isDirectory) {
                startFolder = externalStorage
            }
        }
        return startFolder.absolutePath
    }

    override fun observeDefaultMusicFolder(): Flow<File> {
        return preferences.observeKey(DEFAULT_MUSIC_FOLDER, defaultFolder())
            .map { File(it) }
    }

    override fun getDefaultMusicFolder(): File {
        return File(preferences.getString(DEFAULT_MUSIC_FOLDER, defaultFolder())!!)
    }

    override fun setDefaultMusicFolder(file: File) {
        preferences.edit {
            putString(DEFAULT_MUSIC_FOLDER, file.absolutePath)
        }
    }
}