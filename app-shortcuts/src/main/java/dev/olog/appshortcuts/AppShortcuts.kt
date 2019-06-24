package dev.olog.appshortcuts

import android.content.Context
import android.os.Build
import dev.olog.core.MediaId

class AppShortcuts private constructor(context: Context){

    private val appShortcuts: BaseAppShortcuts by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            AppShortcutsImpl25(context.applicationContext)
        } else {
            AppShortcutsStub(context.applicationContext)
        }
    }

    companion object {

        private var instance: AppShortcuts? = null

        fun instance(context: Context): AppShortcuts {
            if (instance == null) {
                instance = AppShortcuts(context)
            }
            return instance!!
        }
    }

    fun disablePlay() {
        appShortcuts.disablePlay()
    }

    fun enablePlay() {
        appShortcuts.enablePlay()
    }

    fun addDetailShortcut(mediaId: MediaId, title: String) {
        appShortcuts.addDetailShortcut(mediaId, title)
    }

}