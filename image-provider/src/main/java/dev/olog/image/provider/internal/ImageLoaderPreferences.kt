package dev.olog.image.provider.internal

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.image.provider.R
import dev.olog.shared.android.utils.NetworkUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ImageLoaderPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences,
) {

    fun canDownloadImages(): Boolean {
        val downloadMode = prefs.getString(
            context.getString(R.string.prefs_auto_download_images_key),
            context.getString(R.string.prefs_auto_download_images_entry_value_wifi)
        )!!
        return when (downloadMode) {
            context.getString(R.string.prefs_auto_download_images_entry_value_never) -> false
            context.getString(R.string.prefs_auto_download_images_entry_value_wifi) -> NetworkUtils.isOnWiFi(context)
            context.getString(R.string.prefs_auto_download_images_entry_value_always) -> true
            else -> false // should not happen
        }
    }

    fun canAutoCreateImages(): Boolean {
        return prefs.getBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
    }

}