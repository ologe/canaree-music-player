package dev.olog.shared.compose.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets(0,0,0,0),
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Theme.colors.surface,
        contentColor = Theme.m3Colors.onSurface,
        tonalElevation = 0.dp,
        windowInsets = windowInsets,
        content = content
    )
}

@Composable
fun RowScope.BottomNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
        modifier = modifier,
        enabled = true,
        label = null,
        alwaysShowLabel = false,
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        BottomNavigation {
            BottomNavigationItem(
                selected = true,
                onClick = { },
                icon = Icons.Rounded.Home,
            )
            BottomNavigationItem(
                selected = false,
                onClick = { },
                icon = Icons.Rounded.Search,
            )
            BottomNavigationItem(
                selected = false,
                onClick = { },
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
            )
        }
    }
}