package dev.olog.feature.library

import dev.olog.core.Preference
import dev.olog.core.Prefs

interface LibraryPrefs : Prefs {

    fun spanCount(category: TabCategory): Preference<Int>

    val newItemsVisibility: Preference<Boolean>
    val recentPlayedVisibility: Preference<Boolean>

}