package dev.olog.shared.android.theme

import kotlinx.coroutines.flow.Flow

interface QuickActionAmbient {
    val value: QuickAction
    val flow: Flow<QuickAction>
}

enum class QuickAction {
    NONE, PLAY, SHUFFLE
}