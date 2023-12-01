package dev.olog.shared.compose.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val LocalCanareeSpacing = staticCompositionLocalOf<CanareeSpacing> {
    error("LocalCanareeSpacing not set")
}

val LocalScreenSpacing = staticCompositionLocalOf<PaddingValues> {
    error("LocalScreenSpacing not set")
}

@Immutable
data class CanareeSpacing(
    val medium: Dp,
    val mediumSmall: Dp,
    val small: Dp,
    val extraSmall: Dp,
)

@Composable
internal fun spacing(): CanareeSpacing {
    // TODO rename variables??
    return CanareeSpacing(
        medium = 16.dp,
        mediumSmall = 12.dp,
        small = 8.dp,
        extraSmall = 4.dp,
    )
}