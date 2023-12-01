package dev.olog.shared.android.theme

import kotlinx.coroutines.flow.StateFlow

interface HasQuickAction {
    fun observeQuickAction(): StateFlow<QuickAction>
}

enum class QuickAction {
    NONE, PLAY, SHUFFLE
}