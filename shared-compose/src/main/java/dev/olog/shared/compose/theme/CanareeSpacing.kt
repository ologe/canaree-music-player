package dev.olog.shared.compose.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val LocalCanareeSpacing = staticCompositionLocalOf<CanareeSpacing> {
    error("LocalCanareeSpacing not set")
}

@Immutable
data class CanareeSpacing(
    val medium: Dp,
    val mediumSmall: Dp,
    val small: Dp,
    val extraSmall: Dp,
    val listContentPadding: PaddingValues,
    val listHorizontalArrangement: Arrangement.HorizontalOrVertical,
    val listVerticalArrangement: Arrangement.HorizontalOrVertical,
)

@Composable
internal fun spacing(): CanareeSpacing {
    // TODO rename variables??
    return CanareeSpacing(
        medium = 16.dp,
        mediumSmall = 12.dp,
        small = 8.dp,
        extraSmall = 4.dp,
        listContentPadding = PaddingValues(8.dp),
        listHorizontalArrangement = Arrangement.spacedBy(8.dp),
        listVerticalArrangement = Arrangement.spacedBy(8.dp),
    )
}