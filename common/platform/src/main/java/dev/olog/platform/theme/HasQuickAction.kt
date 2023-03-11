package dev.olog.platform.theme

import android.content.Context
import dev.olog.platform.extension.findInContext
import kotlinx.coroutines.channels.ReceiveChannel

fun Context.hasQuickAction(): HasQuickAction {
    return applicationContext.findInContext()
}

interface HasQuickAction {
    fun getQuickAction(): QuickAction
    fun observeQuickAction(): ReceiveChannel<QuickAction>
}

enum class QuickAction {
    NONE, PLAY, SHUFFLE
}