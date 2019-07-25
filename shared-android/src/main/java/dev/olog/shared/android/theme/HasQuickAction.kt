package dev.olog.shared.android.theme

import kotlinx.coroutines.channels.ReceiveChannel

interface HasQuickAction {
    fun getQuickAction(): QuickAction
    fun observeQuickAction(): ReceiveChannel<QuickAction>
}

enum class QuickAction {
    NONE, PLAY, SHUFFLE
}