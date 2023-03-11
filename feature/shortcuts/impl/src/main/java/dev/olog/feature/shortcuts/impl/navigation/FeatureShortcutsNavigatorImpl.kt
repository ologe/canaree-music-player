package dev.olog.feature.shortcuts.impl.navigation

import dev.olog.core.MediaId
import dev.olog.feature.shortcuts.api.FeatureShortcutsNavigator
import dev.olog.feature.shortcuts.impl.AppShortcuts
import javax.inject.Inject

class FeatureShortcutsNavigatorImpl @Inject constructor(
    private val shortcuts: AppShortcuts,
) : FeatureShortcutsNavigator {

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        shortcuts.addDetailShortcut(mediaId, title)
    }
}