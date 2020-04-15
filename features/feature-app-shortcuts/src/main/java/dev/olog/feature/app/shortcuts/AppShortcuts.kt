package dev.olog.feature.app.shortcuts

import dev.olog.domain.MediaId

interface AppShortcuts {

    fun addDetailShortcut(mediaId: MediaId, title: String)

}