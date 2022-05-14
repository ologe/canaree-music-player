package dev.olog.feature.shortcuts.api

import dev.olog.core.MediaId

interface AppShortcuts {

    // todo move to some initializer
    fun setup()

    fun addDetailShortcut(mediaId: MediaId, title: String)

}