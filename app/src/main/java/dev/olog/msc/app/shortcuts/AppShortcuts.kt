package dev.olog.msc.app.shortcuts

import dev.olog.core.MediaId

interface AppShortcuts {

    fun disablePlay()
    fun enablePlay()

    fun addDetailShortcut(mediaId: MediaId, title: String)

}