package dev.olog.feature.library

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import dev.olog.feature.library.layout.manager.SpanCountController
import javax.inject.Inject

internal class LibraryPrefsImpl @Inject constructor(
    private val prefs: PreferenceManager,
    @ApplicationContext private val context: Context
) : LibraryPrefs {

    override fun spanCount(category: TabCategory): Preference<Int> {
        return prefs.create("${category}_span", SpanCountController.getDefaultSpan(context, category))
    }

    override val newItemsVisibility: Preference<Boolean>
        get() = prefs.create(dev.olog.prefskeys.R.string.prefs_show_new_albums_artists_key, true)

    override val recentPlayedVisibility: Preference<Boolean>
        get() = prefs.create(dev.olog.prefskeys.R.string.prefs_show_recent_albums_artists_key, true)
}