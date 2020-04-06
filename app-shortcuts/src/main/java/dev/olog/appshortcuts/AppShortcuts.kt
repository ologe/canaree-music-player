package dev.olog.appshortcuts

import android.content.Context
import dev.olog.domain.MediaId
import dev.olog.domain.schedulers.Schedulers

class AppShortcuts private constructor(
    context: Context,
    schedulers: Schedulers
) {

    private val appShortcuts = AppShortcutsImp(context.applicationContext, schedulers)

    companion object {
        @JvmStatic
        private var instance: AppShortcuts? = null

        @JvmStatic
        fun instance(context: Context, schedulers: Schedulers): AppShortcuts {
            if (instance == null) {
                instance = AppShortcuts(context, schedulers)
            }
            return instance!!
        }
    }

    fun addDetailShortcut(mediaId: MediaId, title: String) {
        appShortcuts.addDetailShortcut(mediaId, title)
    }

}