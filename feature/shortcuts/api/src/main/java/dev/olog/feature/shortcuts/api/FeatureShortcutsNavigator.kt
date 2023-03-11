package dev.olog.feature.shortcuts.api

import dev.olog.core.MediaId

interface FeatureShortcutsNavigator {

    fun addDetailShortcut(mediaId: MediaId, title: String)

}