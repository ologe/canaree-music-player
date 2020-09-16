package dev.olog.shared.components

import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable

@Composable
fun HighEmphasis(content: @Composable () -> Unit) {
    ProvideEmphasis(
        emphasis = EmphasisAmbient.current.high,
        content = content
    )
}

@Composable
fun MediumEmphasis(content: @Composable () -> Unit) {
    ProvideEmphasis(
        emphasis = EmphasisAmbient.current.medium,
        content = content
    )
}

@Composable
fun DisabledEmphasis(content: @Composable () -> Unit) {
    ProvideEmphasis(
        emphasis = EmphasisAmbient.current.disabled,
        content = content
    )
}