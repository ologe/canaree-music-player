package dev.olog.domain

import dev.olog.domain.mediaid.MediaId

interface AppShortcuts {

    // stub
    fun initialize(){}

    fun addDetailShortcut(mediaId: MediaId, title: String)

}