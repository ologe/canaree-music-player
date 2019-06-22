package dev.olog.msc.data.prefs.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.R
import dev.olog.msc.domain.entity.UserCredentials
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.utils.k.extension.safeGetCanonicalPath
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences,
    private val rxPreferences: RxSharedPreferences

) : AppPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val SLEEP_TIME = "$TAG.SLEEP_TIME"
        private const val SLEEP_FROM = "$TAG.FROM_WHEN"


        private const val LAST_FM_USERNAME = "$TAG.LAST_FM_USERNAME"
        private const val LAST_FM_PASSWORD = "$TAG.LAST_FM_PASSWORD"

        private const val SYNC_ADJUSTMENT = "$TAG.SYNC_ADJUSTMENT"

        private const val DEFAULT_MUSIC_FOLDER = "$TAG.DEFAULT_MUSIC_FOLDER"
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

    override fun observePlayerControlsVisibility(): Observable<Boolean> {
        val key = context.getString(R.string.prefs_player_controls_visibility_key)
        return rxPreferences.getBoolean(key, false)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun setDefault(): Completable {
        return Completable.create { emitter ->
            hideQuickAction()
            setDefaultVisibleSections()
            hideClassicPlayerControls()
            setDefaultAutoDownloadImages()
            setDefaultTheme()
            setLastFmCredentials(UserCredentials("", ""))
            setDefaultFolderView()
            setDefaultMusicFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
            setDefaultAccentColor()
            setDefaultLibraryAlbumArtistVisibility()
            setDefaultPodcastVisibility()
            setDefaultAdaptiveColors()
            setDefaultLockscreenArtwork()

            emitter.onComplete()
        }
    }

    private fun setDefaultLockscreenArtwork(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_lockscreen_artwork_key), false)
        }
    }

    private fun setDefaultAdaptiveColors(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
        }
    }

    private fun setDefaultLibraryAlbumArtistVisibility(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_show_new_albums_artists_key), true)
            putBoolean(context.getString(R.string.prefs_show_recent_albums_artists_key), true)
        }
    }

    private fun setDefaultPodcastVisibility(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_show_podcasts_key), true)
        }
    }

    private fun setDefaultAccentColor(){
        preferences.edit {
            putInt(context.getString(R.string.prefs_color_accent_key), R.color.accent)
        }
    }

    override fun observeAutoCreateImages(): Observable<Boolean> {
        return rxPreferences.getBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun setDefaultFolderView(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
        }
    }

    private fun setDefaultAutoDownloadImages(){
        preferences.edit {
            putString(context.getString(R.string.prefs_auto_download_images_key), context.getString(R.string.prefs_auto_download_images_entry_value_wifi))
            putBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
        }
    }

    private fun hideQuickAction(){
        preferences.edit {
            putString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        }
    }

    private fun setDefaultVisibleSections(){
        preferences.edit {
            val default = context.resources.getStringArray(R.array.prefs_detail_sections_entry_values_default).toSet()
            putStringSet(context.getString(R.string.prefs_detail_sections_key), default)
        }
    }

    private fun hideClassicPlayerControls(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_player_controls_visibility_key), false)
        }
    }

    private fun setDefaultTheme(){
        preferences.edit {
            putString(context.getString(R.string.prefs_appearance_key), context.getString(R.string.prefs_appearance_entry_value_default))
            putString(context.getString(R.string.prefs_dark_mode_key), context.getString(R.string.prefs_dark_mode_2_value_follow_system))
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
    override fun observeLastFmCredentials(): Observable<UserCredentials> {
        return rxPreferences.getString(LAST_FM_USERNAME, "")
                .asObservable()
                .map { UserCredentials(
                        it,
                        preferences.getString(LAST_FM_PASSWORD, "")!!
                ) }
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

    override fun getSyncAdjustment(): Long {
        return preferences.getLong(SYNC_ADJUSTMENT, 0)
    }

    override fun setSyncAdjustment(value: Long) {
        preferences.edit { putLong(SYNC_ADJUSTMENT, value) }
    }

    private fun defaultFolder(): String {
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        var startFolder = File(File.separator)
        if (musicDir.exists() && musicDir.isDirectory){
            startFolder = musicDir
        } else {
            val externalStorage = Environment.getExternalStorageDirectory()
            if (externalStorage.exists() && externalStorage.isDirectory){
                startFolder = externalStorage
            }
        }
        return startFolder.path
    }

    override fun observeDefaultMusicFolder(): Observable<File> {
        return rxPreferences.getString(DEFAULT_MUSIC_FOLDER, defaultFolder())
                .asObservable()
                .map { File(it) }
    }

    override fun getDefaultMusicFolder(): File {
        return File(preferences.getString(DEFAULT_MUSIC_FOLDER, defaultFolder()))
    }

    override fun setDefaultMusicFolder(file: File) {
        preferences.edit {
            putString(DEFAULT_MUSIC_FOLDER, file.safeGetCanonicalPath())
        }
    }
    override fun isAdaptiveColorEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
    }
}