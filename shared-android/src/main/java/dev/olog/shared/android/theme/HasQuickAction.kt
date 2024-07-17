package dev.olog.shared.android.theme

import kotlinx.coroutines.flow.StateFlow

interface HasQuickAction {
    fun getQuickAction(): QuickAction
    fun observeQuickAction(): StateFlow<QuickAction>
}

enum class QuickAction {
    NONE, PLAY, SHUFFLE
}