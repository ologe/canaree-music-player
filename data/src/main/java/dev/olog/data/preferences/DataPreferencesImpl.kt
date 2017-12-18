package dev.olog.data.preferences

import android.content.SharedPreferences
import dev.olog.data.utils.edit
import dev.olog.domain.gateway.prefs.DataPreferencesGateway
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataPreferencesImpl @Inject constructor(
        private val preferences: SharedPreferences

) : DataPreferencesGateway {

    companion object {
        private const val TAG = "DataPreferencesImpl"
        private const val FOLDER_IMAGES = "$TAG.FOLDER_IMAGES"
        private const val PLAYLIST_IMAGES = "$TAG.PLAYLIST_IMAGES"
        private const val ARTIST_IMAGES = "$TAG.ARTIST_IMAGES"
        private const val GENRE_IMAGES = "$TAG.GENRE_IMAGES"
    }

    override fun areFolderImagesCreated(): AtomicBoolean {
        return AtomicBoolean(preferences.getBoolean(FOLDER_IMAGES, false))
    }

    override fun arePlaylistImagesCreated(): AtomicBoolean {
        return AtomicBoolean(preferences.getBoolean(PLAYLIST_IMAGES, false))
    }

    override fun areArtistImagesCreated(): AtomicBoolean {
        return AtomicBoolean(preferences.getBoolean(ARTIST_IMAGES, false))
    }

    override fun areGenreImagesCreated(): AtomicBoolean {
        return AtomicBoolean(preferences.getBoolean(GENRE_IMAGES, false))
    }

    override fun setFolderImagesCreated() {
        return preferences.edit { putBoolean(FOLDER_IMAGES, true) }
    }

    override fun setPlaylistImagesCreated() {
        return preferences.edit { putBoolean(PLAYLIST_IMAGES, true) }
    }

    override fun setArtistImagesCreated() {
        return preferences.edit { putBoolean(ARTIST_IMAGES, true) }
    }

    override fun setGenreImagesCreated() {
        return preferences.edit { putBoolean(GENRE_IMAGES, true) }
    }
}