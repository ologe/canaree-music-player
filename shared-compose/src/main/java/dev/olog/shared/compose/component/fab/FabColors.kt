package dev.olog.shared.compose.component.fab

import androidx.compose.runtime.Composable
import dev.olog.shared.compose.Theme

enum class FabColors {
    Primary,
    Secondary,
    Tertiary;

    @Composable
    internal fun containerColor() = when (this) {
        Primary -> Theme.m3Colors.primaryContainer
        Secondary -> Theme.m3Colors.secondaryContainer
        Tertiary -> Theme.m3Colors.tertiaryContainer
    }

    companion object {

        fun fromInt(value: Int): FabColors = when (value) {
            0 -> Primary
            1 -> Secondary
            2 -> Tertiary
            else -> Primary
        }

    }

}