package dev.olog.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

fun Modifier.statusBarsPadding(plus: Dp): Modifier {
    return this
        .statusBarsPadding()
        .padding(top = plus)
}