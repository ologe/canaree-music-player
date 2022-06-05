package dev.olog.compose

import androidx.compose.material.LocalContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// use values from [androidx.compose.material.HighContrastContentAlpha]

@Composable
fun WithHighEmphasys(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentAlpha provides 1f,
        content = content,
    )
}

@Composable
fun WithMediumEmphasys(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentAlpha provides .74f,
        content = content,
    )
}

@Composable
fun WithDisabledEmphasys(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentAlpha provides .38f,
        content = content,
    )
}