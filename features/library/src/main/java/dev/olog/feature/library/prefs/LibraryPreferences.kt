package dev.olog.feature.library.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.MediaIdCategory
import dev.olog.feature.library.SpanCountController
import dev.olog.feature.library.model.TabCategory
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LibraryPreferences @Inject constructor(
    private val preferences: SharedPreferences
) {

    fun getSpanCount(category: MediaIdCategory): Int {
        return preferences.getInt("${category}_library_span", getDefaultSpan(category))
    }

    fun observeSpanCount(category: MediaIdCategory): Flow<Int> {
        return preferences.observeKey("${category}_library_span", getDefaultSpan(category))
    }

    fun setSpanCount(category: MediaIdCategory, spanCount: Int) {
        preferences.edit {
            putInt("${category}_library_span", spanCount)
        }
    }

    fun getSpanCount(category: TabCategory): Int {
        return preferences.getInt("${category}_span", SpanCountController.getDefaultSpan(category))
    }

    fun observeSpanCount(category: TabCategory): Flow<Int> {
        return preferences.observeKey("${category}_span", SpanCountController.getDefaultSpan(category))
    }

    fun setSpanCount(category: TabCategory, spanCount: Int) {
        preferences.edit {
            putInt("${category}_span", spanCount)
        }
    }

    private fun getDefaultSpan(category: MediaIdCategory): Int {
        return when (category) {
            MediaIdCategory.FOLDERS -> 3
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> 3
            MediaIdCategory.PODCASTS -> 1
            MediaIdCategory.ALBUMS -> 2
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_AUTHORS -> 3
            MediaIdCategory.GENRES -> 3
            MediaIdCategory.SONGS -> TODO()
            MediaIdCategory.SPOTIFY_ALBUMS -> TODO()
            MediaIdCategory.SPOTIFY_TRACK -> TODO()
        }
    }

}