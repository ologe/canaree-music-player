package dev.olog.data.mediastore

import android.content.Context
import android.content.SharedPreferences
import android.provider.MediaStore
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.platform.BuildVersion
import javax.inject.Inject

class MediaStoreVersionPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences,
) {

    companion object {
        private const val MEDIA_STORE_VERSION = "media_store_version"
    }


    suspend fun onVersionChanged(block: suspend () -> Unit) {
        val cached = prefs.getString(MEDIA_STORE_VERSION, null)
        val current = getMediaStoreVersion() // TODO needs to check also generation?

        if (cached == null || current != cached) {
            block()
            prefs.edit { putString(MEDIA_STORE_VERSION, current) }
        }
    }

    private fun getMediaStoreVersion(): String {
        if (BuildVersion.isQ()) {
            return MediaStore.getVersion(context, MediaStore.VOLUME_EXTERNAL)
        }
        return MediaStore.getVersion(context)
    }

}