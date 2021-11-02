package dev.olog.feature.library

import dev.olog.core.Preference

interface LibraryPrefs {

    fun spanCount(category: TabCategory): Preference<Int>

    val newItemsVisibility: Preference<Boolean>
    val recentPlayedVisibility: Preference<Boolean>

}