package dev.olog.msc.app.shortcuts

import android.arch.lifecycle.Lifecycle
import android.content.Context
import dev.olog.msc.dagger.qualifier.ProcessLifecycle

class AppShortcutsStub(
        context: Context,
        @ProcessLifecycle lifecycle: Lifecycle

) : BaseAppShortcuts(context, lifecycle) {

    override fun disablePlay() {
    }

    override fun enablePlay() {
    }
}