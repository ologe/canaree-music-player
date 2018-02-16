package dev.olog.msc.app.shortcuts

import dev.olog.msc.utils.MediaId

interface AppShortcuts {

    fun disablePlay()
    fun enablePlay()

    fun addDetailShortcut(mediaId: MediaId, title: String, image: String)

}