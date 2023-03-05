package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext
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